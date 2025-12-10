package com.whatthefork.userservice.auth.service;

import com.whatthefork.userservice.auth.dto.LoginRequest;
import com.whatthefork.userservice.auth.dto.TokenResponse;
import com.whatthefork.userservice.auth.entity.RefreshToken;
import com.whatthefork.userservice.auth.repository.RefreshTokenRepository;
import com.whatthefork.userservice.command.entity.User;
import com.whatthefork.userservice.command.entity.UserRole;
import com.whatthefork.userservice.command.repository.UserRepository;
import com.whatthefork.userservice.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService
 *
 * Testing Strategy:
 * - Uses Mockito to mock dependencies (repositories, password encoder, JWT provider)
 * - Tests each method's success scenarios and failure scenarios
 * - Verifies correct interactions with dependencies
 * - Validates exception handling for authentication failures
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private LoginRequest loginRequest;
    private String testAccessToken;
    private String testRefreshToken;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@example.com")
                .password("encodedPassword123")
                .name("Test User")
                .positionCode(1)
                .deptId("DEPT001")
                .isDeptLeader(false)
                .isAdmin(false)
                .role(UserRole.USER)
                .build();

        loginRequest = new LoginRequest("test@example.com", "plainPassword123");
        testAccessToken = "test.access.token";
        testRefreshToken = "test.refresh.token";
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("성공: 올바른 자격 증명으로 로그인하면 토큰을 반환한다")
        void login_WithValidCredentials_ReturnsTokenResponse() {
            // Given: Valid user credentials
            when(userRepository.findByEmail(loginRequest.getEmail()))
                    .thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPassword()))
                    .thenReturn(true);
            when(jwtTokenProvider.createToken(testUser.getEmail(), testUser.getRole().name(), testUser.getId()))
                    .thenReturn(testAccessToken);
            when(jwtTokenProvider.createRefreshToken(testUser.getEmail(), testUser.getRole().name(), testUser.getId()))
                    .thenReturn(testRefreshToken);
            when(jwtTokenProvider.getRefreshExpiration())
                    .thenReturn(604800000L);

            // When: User attempts to login
            TokenResponse response = authService.login(loginRequest);

            // Then: Access and refresh tokens are returned
            assertThat(response).isNotNull();
            assertThat(response.getAccessToken()).isEqualTo(testAccessToken);
            assertThat(response.getRefreshToken()).isEqualTo(testRefreshToken);

            // Verify: Password was checked and tokens were created
            verify(passwordEncoder).matches(loginRequest.getPassword(), testUser.getPassword());
            verify(jwtTokenProvider).createToken(testUser.getEmail(), testUser.getRole().name(), testUser.getId());
            verify(jwtTokenProvider).createRefreshToken(testUser.getEmail(), testUser.getRole().name(), testUser.getId());

            // Verify: Refresh token was saved to database
            verify(refreshTokenRepository).save(argThat(token ->
                token.getUsername().equals(testUser.getEmail()) &&
                token.getToken().equals(testRefreshToken)
            ));
        }

        @Test
        @DisplayName("실패: 존재하지 않는 이메일로 로그인 시도 시 BadCredentialsException 발생")
        void login_WithNonExistentEmail_ThrowsBadCredentialsException() {
            // Given: Email does not exist in database
            when(userRepository.findByEmail(loginRequest.getEmail()))
                    .thenReturn(Optional.empty());

            // When & Then: Login attempt throws BadCredentialsException
            assertThatThrownBy(() -> authService.login(loginRequest))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessage("올바르지 않은 아이디 혹은 비밀번호");

            // Verify: Password was never checked
            verify(passwordEncoder, never()).matches(anyString(), anyString());
        }

        @Test
        @DisplayName("실패: 잘못된 비밀번호로 로그인 시도 시 BadCredentialsException 발생")
        void login_WithIncorrectPassword_ThrowsBadCredentialsException() {
            // Given: User exists but password doesn't match
            when(userRepository.findByEmail(loginRequest.getEmail()))
                    .thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPassword()))
                    .thenReturn(false);

            // When & Then: Login attempt throws BadCredentialsException
            assertThatThrownBy(() -> authService.login(loginRequest))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessage("올바르지 않은 아이디 혹은 비밀번호");

            // Verify: Tokens were never created
            verify(jwtTokenProvider, never()).createToken(anyString(), anyString(), anyLong());
            verify(refreshTokenRepository, never()).save(any());
        }

        @Test
        @DisplayName("성공: 관리자 계정으로 로그인 시 ADMIN 역할로 토큰 생성")
        void login_WithAdminUser_CreatesTokenWithAdminRole() {
            // Given: Admin user credentials
            User adminUser = User.builder()
                    .email("admin@example.com")
                    .password("encodedPassword123")
                    .name("Admin User")
                    .positionCode(5)
                    .deptId("ADMIN")
                    .isDeptLeader(true)
                    .isAdmin(true)
                    .role(UserRole.ADMIN)
                    .build();
            LoginRequest adminLoginRequest = new LoginRequest("admin@example.com", "adminPassword");

            when(userRepository.findByEmail(adminLoginRequest.getEmail()))
                    .thenReturn(Optional.of(adminUser));
            when(passwordEncoder.matches(adminLoginRequest.getPassword(), adminUser.getPassword()))
                    .thenReturn(true);
            when(jwtTokenProvider.createToken(adminUser.getEmail(), adminUser.getRole().name(), adminUser.getId()))
                    .thenReturn(testAccessToken);
            when(jwtTokenProvider.createRefreshToken(adminUser.getEmail(), adminUser.getRole().name(), adminUser.getId()))
                    .thenReturn(testRefreshToken);
            when(jwtTokenProvider.getRefreshExpiration())
                    .thenReturn(604800000L);

            // When: Admin user logs in
            TokenResponse response = authService.login(adminLoginRequest);

            // Then: Tokens are created with ADMIN role
            assertThat(response).isNotNull();
            verify(jwtTokenProvider).createToken(adminUser.getEmail(), "ADMIN", adminUser.getId());
            verify(jwtTokenProvider).createRefreshToken(adminUser.getEmail(), "ADMIN", adminUser.getId());
        }
    }

    @Nested
    @DisplayName("Refresh Token Tests")
    class RefreshTokenTests {

        private RefreshToken storedRefreshToken;

        @BeforeEach
        void setUp() {
            storedRefreshToken = RefreshToken.builder()
                    .username(testUser.getEmail())
                    .token(testRefreshToken)
                    .expiryDate(new Date(System.currentTimeMillis() + 604800000L))
                    .build();
        }

        @Test
        @DisplayName("성공: 유효한 리프레시 토큰으로 새로운 액세스 토큰 발급")
        void refreshToken_WithValidToken_ReturnsNewTokens() {
            // Given: Valid refresh token
            when(jwtTokenProvider.validateToken(testRefreshToken))
                    .thenReturn(true);
            when(jwtTokenProvider.getUsernameFromJWT(testRefreshToken))
                    .thenReturn(testUser.getEmail());
            when(refreshTokenRepository.findById(testUser.getEmail()))
                    .thenReturn(Optional.of(storedRefreshToken));
            when(userRepository.findByEmail(testUser.getEmail()))
                    .thenReturn(Optional.of(testUser));
            when(jwtTokenProvider.createToken(testUser.getEmail(), testUser.getRole().name(), testUser.getId()))
                    .thenReturn("new.access.token");
            when(jwtTokenProvider.createRefreshToken(testUser.getEmail(), testUser.getRole().name(), testUser.getId()))
                    .thenReturn("new.refresh.token");
            when(jwtTokenProvider.getRefreshExpiration())
                    .thenReturn(604800000L);

            // When: Refresh token is used
            TokenResponse response = authService.refreshToken(testRefreshToken);

            // Then: New tokens are returned
            assertThat(response).isNotNull();
            assertThat(response.getAccessToken()).isEqualTo("new.access.token");
            assertThat(response.getRefreshToken()).isEqualTo("new.refresh.token");

            // Verify: Token validation occurred and new tokens were created
            verify(jwtTokenProvider).validateToken(testRefreshToken);
            verify(refreshTokenRepository).save(argThat(token ->
                token.getUsername().equals(testUser.getEmail()) &&
                token.getToken().equals("new.refresh.token")
            ));
        }

        @Test
        @DisplayName("실패: 데이터베이스에 저장되지 않은 리프레시 토큰으로 요청 시 예외 발생")
        void refreshToken_WithNonExistentToken_ThrowsException() {
            // Given: Token is valid but not stored in database
            when(jwtTokenProvider.validateToken(testRefreshToken))
                    .thenReturn(true);
            when(jwtTokenProvider.getUsernameFromJWT(testRefreshToken))
                    .thenReturn(testUser.getEmail());
            when(refreshTokenRepository.findById(testUser.getEmail()))
                    .thenReturn(Optional.empty());

            // When & Then: Refresh attempt throws exception
            assertThatThrownBy(() -> authService.refreshToken(testRefreshToken))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessage("해당 유저로 조회되는 리프레시 토큰 없음");

            // Verify: No new tokens were created
            verify(jwtTokenProvider, never()).createToken(anyString(), anyString(), anyLong());
        }

        @Test
        @DisplayName("실패: 저장된 토큰과 제공된 토큰이 일치하지 않으면 예외 발생")
        void refreshToken_WithMismatchedToken_ThrowsException() {
            // Given: Stored token doesn't match provided token
            String wrongToken = "wrong.refresh.token";
            when(jwtTokenProvider.validateToken(wrongToken))
                    .thenReturn(true);
            when(jwtTokenProvider.getUsernameFromJWT(wrongToken))
                    .thenReturn(testUser.getEmail());
            when(refreshTokenRepository.findById(testUser.getEmail()))
                    .thenReturn(Optional.of(storedRefreshToken));

            // When & Then: Refresh attempt throws exception
            assertThatThrownBy(() -> authService.refreshToken(wrongToken))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessage("리프레시 토큰 일치하지 않음");
        }

        @Test
        @DisplayName("실패: 만료된 리프레시 토큰으로 요청 시 예외 발생")
        void refreshToken_WithExpiredToken_ThrowsException() {
            // Given: Refresh token is expired
            RefreshToken expiredToken = RefreshToken.builder()
                    .username(testUser.getEmail())
                    .token(testRefreshToken)
                    .expiryDate(new Date(System.currentTimeMillis() - 1000L)) // Expired
                    .build();

            when(jwtTokenProvider.validateToken(testRefreshToken))
                    .thenReturn(true);
            when(jwtTokenProvider.getUsernameFromJWT(testRefreshToken))
                    .thenReturn(testUser.getEmail());
            when(refreshTokenRepository.findById(testUser.getEmail()))
                    .thenReturn(Optional.of(expiredToken));

            // When & Then: Refresh attempt throws exception
            assertThatThrownBy(() -> authService.refreshToken(testRefreshToken))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessage("리프레시 토큰 유효시간 만료");
        }

        @Test
        @DisplayName("실패: 유효하지 않은 사용자로 리프레시 토큰 요청 시 예외 발생")
        void refreshToken_WithNonExistentUser_ThrowsException() {
            // Given: Token is valid but user doesn't exist
            when(jwtTokenProvider.validateToken(testRefreshToken))
                    .thenReturn(true);
            when(jwtTokenProvider.getUsernameFromJWT(testRefreshToken))
                    .thenReturn(testUser.getEmail());
            when(refreshTokenRepository.findById(testUser.getEmail()))
                    .thenReturn(Optional.of(storedRefreshToken));
            when(userRepository.findByEmail(testUser.getEmail()))
                    .thenReturn(Optional.empty());

            // When & Then: Refresh attempt throws exception
            assertThatThrownBy(() -> authService.refreshToken(testRefreshToken))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessage("해당 리프레시 토큰을 위한 유저 없음");
        }
    }

    @Nested
    @DisplayName("Logout Tests")
    class LogoutTests {

        @Test
        @DisplayName("성공: 유효한 리프레시 토큰으로 로그아웃하면 토큰이 삭제된다")
        void logout_WithValidToken_DeletesRefreshToken() {
            // Given: Valid refresh token
            when(jwtTokenProvider.validateToken(testRefreshToken))
                    .thenReturn(true);
            when(jwtTokenProvider.getUsernameFromJWT(testRefreshToken))
                    .thenReturn(testUser.getEmail());

            // When: User logs out
            authService.logout(testRefreshToken);

            // Then: Refresh token is deleted from database
            verify(jwtTokenProvider).validateToken(testRefreshToken);
            verify(jwtTokenProvider).getUsernameFromJWT(testRefreshToken);
            verify(refreshTokenRepository).deleteById(testUser.getEmail());
        }

        @Test
        @DisplayName("성공: 로그아웃 시 토큰 검증이 먼저 수행된다")
        void logout_ValidatesTokenFirst() {
            // Given: Token that will be validated
            when(jwtTokenProvider.validateToken(testRefreshToken))
                    .thenReturn(true);
            when(jwtTokenProvider.getUsernameFromJWT(testRefreshToken))
                    .thenReturn(testUser.getEmail());

            // When: User logs out
            authService.logout(testRefreshToken);

            // Then: Token validation occurs before deletion
            verify(jwtTokenProvider).validateToken(testRefreshToken);
            verify(refreshTokenRepository).deleteById(anyString());
        }
    }
}
