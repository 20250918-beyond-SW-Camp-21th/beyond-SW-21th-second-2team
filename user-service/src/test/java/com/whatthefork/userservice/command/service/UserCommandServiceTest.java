package com.whatthefork.userservice.command.service;

import com.whatthefork.userservice.command.dto.ChangePasswordRequest;
import com.whatthefork.userservice.command.dto.UserCreateRequest;
import com.whatthefork.userservice.command.entity.User;
import com.whatthefork.userservice.command.entity.UserRole;
import com.whatthefork.userservice.command.repository.UserRepository;
import com.whatthefork.userservice.exception.DuplicateEmailException;
import com.whatthefork.userservice.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserCommandService userCommandService;

    private User testUser;
    private UserCreateRequest userCreateRequest;
    private String plainPassword = "password123";
    private String encodedPassword = "encodedPassword123";

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@example.com")
                .password(encodedPassword)
                .name("Test User")
                .positionCode(1)
                .deptId("DEPT001")
                .isDeptLeader(false)
                .isAdmin(false)
                .role(UserRole.USER)
                .build();

        userCreateRequest = new UserCreateRequest(
                "Test User",
                "test@example.com",
                plainPassword,
                1,
                "DEPT001",
                false,
                false
        );
    }

    @Nested
    @DisplayName("Register User Tests")
    class RegisterUserTests {

        @Test
        @DisplayName("유효한 정보로 일반 사용자 등록 시 USER 역할이 부여된다")
        void registerUser_WithValidInfo_CreatesUserWithUserRole() {
            // Given: Valid user creation request for regular user
            when(userRepository.existsByEmail(userCreateRequest.getEmail()))
                    .thenReturn(false);
            when(passwordEncoder.encode(plainPassword))
                    .thenReturn(encodedPassword);

            // When: User registration is performed
            userCommandService.registerUser(userCreateRequest);

            // Then: User is saved with USER role
            verify(userRepository).existsByEmail(userCreateRequest.getEmail());
            verify(passwordEncoder).encode(plainPassword);
            verify(userRepository).save(argThat(user ->
                    user.getEmail().equals(userCreateRequest.getEmail()) &&
                            user.getName().equals(userCreateRequest.getName()) &&
                            user.getPassword().equals(encodedPassword) &&
                            user.getRole() == UserRole.USER &&
                            !user.isAdmin()
            ));
        }

        @Test
        @DisplayName("admin값이 true인 경우 ADMIN 역할이 부여된다")
        void registerUser_WithAdminFlag_CreatesUserWithAdminRole() {
            // Given: User creation request with admin flag
            UserCreateRequest adminRequest = new UserCreateRequest(
                    "Admin User",
                    "admin@example.com",
                    plainPassword,
                    5,
                    "ADMIN",
                    true,
                    true
            );

            when(userRepository.existsByEmail(adminRequest.getEmail()))
                    .thenReturn(false);
            when(passwordEncoder.encode(plainPassword))
                    .thenReturn(encodedPassword);

            // When: Admin user registration is performed
            userCommandService.registerUser(adminRequest);

            // Then: User is saved with ADMIN role
            verify(userRepository).save(argThat(user ->
                    user.getEmail().equals(adminRequest.getEmail()) &&
                            user.getRole() == UserRole.ADMIN &&
                            user.isAdmin()
            ));
        }

        @Test
        @DisplayName("부서장 값이 올바르게 설정된다")
        void registerUser_WithDeptLeaderFlag_SetsCorrectFlag() {
            // Given: User creation request with dept leader flag
            UserCreateRequest deptLeaderRequest = new UserCreateRequest(
                    "Dept Leader",
                    "leader@example.com",
                    plainPassword,
                    3,
                    "DEPT001",
                    true,
                    false
            );

            when(userRepository.existsByEmail(deptLeaderRequest.getEmail()))
                    .thenReturn(false);
            when(passwordEncoder.encode(plainPassword))
                    .thenReturn(encodedPassword);

            // When: Dept leader registration is performed
            userCommandService.registerUser(deptLeaderRequest);

            // Then: User is saved with dept leader flag set
            verify(userRepository).save(argThat(user ->
                    user.isDeptLeader() &&
                            user.getDeptId().equals("DEPT001")
            ));
        }

        @Test
        @DisplayName("비밀번호가 암호화되어 저장된다")
        void registerUser_EncodesPassword() {
            // Given: Valid user creation request
            when(userRepository.existsByEmail(userCreateRequest.getEmail()))
                    .thenReturn(false);
            when(passwordEncoder.encode(plainPassword))
                    .thenReturn(encodedPassword);

            // When: User registration is performed
            userCommandService.registerUser(userCreateRequest);

            // Then: Password encoder is called and encoded password is saved
            verify(passwordEncoder).encode(plainPassword);
            verify(userRepository).save(argThat(user ->
                    user.getPassword().equals(encodedPassword) &&
                            !user.getPassword().equals(plainPassword)
            ));
        }

        @Test
        @DisplayName("중복된 이메일로 등록 시도 시 DuplicateEmailException 발생")
        void registerUser_WithDuplicateEmail_ThrowsDuplicateEmailException() {
            // Given: Email already exists in database
            when(userRepository.existsByEmail(userCreateRequest.getEmail()))
                    .thenReturn(true);

            // When & Then: Registration attempt throws DuplicateEmailException
            assertThatThrownBy(() -> userCommandService.registerUser(userCreateRequest))
                    .isInstanceOf(DuplicateEmailException.class)
                    .hasMessage("이미 사용 중인 이메일입니다: " + userCreateRequest.getEmail());

            // Verify: User was never saved
            verify(userRepository, never()).save(any());
            verify(passwordEncoder, never()).encode(anyString());
        }

        @Test
        @DisplayName("모든 사용자 정보 필드가 올바르게 저장된다")
        void registerUser_SavesAllUserFields() {
            // Given: Complete user creation request
            when(userRepository.existsByEmail(userCreateRequest.getEmail()))
                    .thenReturn(false);
            when(passwordEncoder.encode(plainPassword))
                    .thenReturn(encodedPassword);

            // When: User registration is performed
            userCommandService.registerUser(userCreateRequest);

            // Then: All fields are correctly saved
            verify(userRepository).save(argThat(user ->
                    user.getName().equals(userCreateRequest.getName()) &&
                            user.getEmail().equals(userCreateRequest.getEmail()) &&
                            user.getPositionCode() == userCreateRequest.getPositionCode() &&
                            user.getDeptId().equals(userCreateRequest.getDeptId()) &&
                            user.isDeptLeader() == userCreateRequest.isDeptLeader() &&
                            user.isAdmin() == userCreateRequest.isAdmin()
            ));
        }
    }

    @Nested
    @DisplayName("Delete User Tests")
    class DeleteUserTests {

        @Test
        @DisplayName("존재하는 사용자 ID로 삭제 시 사용자가 삭제된다")
        void deleteUser_WithExistingUserId_DeletesUser() {
            // Given: Existing user in database
            Long userId = 1L;
            when(userRepository.findById(userId))
                    .thenReturn(Optional.of(testUser));

            // When: User deletion is performed
            userCommandService.deleteUser(userId);

            // Then: User is deleted from database
            verify(userRepository).findById(userId);
            verify(userRepository).delete(testUser);
        }

        @Test
        @DisplayName("존재하지 않는 사용자 ID로 삭제 시도 시 UserNotFoundException 발생")
        void deleteUser_WithNonExistentUserId_ThrowsUserNotFoundException() {
            // Given: User does not exist in database
            Long userId = 999L;
            when(userRepository.findById(userId))
                    .thenReturn(Optional.empty());

            // When & Then: Deletion attempt throws UserNotFoundException
            assertThatThrownBy(() -> userCommandService.deleteUser(userId))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("사용자를 찾을 수 없습니다: ID = " + userId);

            // Verify: Delete was never called
            verify(userRepository, never()).delete(any());
        }

    }

    @Nested
    @DisplayName("Change Own Password Tests")
    class ChangeOwnPasswordTests {

        private ChangePasswordRequest changePasswordRequest;
        private String newPlainPassword = "newPassword456";
        private String newEncodedPassword = "encodedNewPassword456";

        @BeforeEach
        void setUp() {
            changePasswordRequest = new ChangePasswordRequest(
                    plainPassword,
                    newPlainPassword
            );
        }

        @Test
        @DisplayName("잘못된 현재 비밀번호로 변경 시도 시 BadCredentialsException 발생")
        void changeOwnPassword_WithIncorrectCurrentPassword_ThrowsBadCredentialsException() {
            // Given: User exists but current password doesn't match
            Long userId = 1L;
            when(userRepository.findById(userId))
                    .thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(plainPassword, testUser.getPassword()))
                    .thenReturn(false);

            // When & Then: Password change attempt throws BadCredentialsException
            assertThatThrownBy(() -> userCommandService.changeOwnPassword(userId, changePasswordRequest))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessage("현재 비밀번호가 올바르지 않습니다");

            // Verify: Password was never updated
            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("존재하지 않는 사용자 ID로 비밀번호 변경 시도 시 UserNotFoundException 발생")
        void changeOwnPassword_WithNonExistentUserId_ThrowsUserNotFoundException() {
            // Given: User does not exist
            Long userId = 999L;
            when(userRepository.findById(userId))
                    .thenReturn(Optional.empty());

            // When & Then: Password change attempt throws UserNotFoundException
            assertThatThrownBy(() -> userCommandService.changeOwnPassword(userId, changePasswordRequest))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("사용자를 찾을 수 없습니다: ID = " + userId);

            // Verify: Password check was never performed
            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(passwordEncoder, never()).encode(anyString());
        }

        @Test
        @DisplayName("새 비밀번호가 암호화되어 저장된다")
        void changeOwnPassword_EncodesNewPassword() {
            // Given: Valid password change request
            Long userId = 1L;
            when(userRepository.findById(userId))
                    .thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(plainPassword, testUser.getPassword()))
                    .thenReturn(true);
            when(passwordEncoder.encode(newPlainPassword))
                    .thenReturn(newEncodedPassword);

            // When: Password change is performed
            userCommandService.changeOwnPassword(userId, changePasswordRequest);

            // Then: New password is encoded before saving
            verify(passwordEncoder).encode(newPlainPassword);
            verify(userRepository).save(argThat(user ->
                    !user.getPassword().equals(newPlainPassword) &&
                            user.getPassword().equals(newEncodedPassword)
            ));
        }
    }
}