package com.whatthefork.gateway.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class GatewayJwtTokenProviderTest {

    private GatewayJwtTokenProvider jwtTokenProvider;
    private String testSecret = "+i12AQFwws/Del1pGAOB0imXQLGaCNd7dyMF41Mxe6k=";
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new GatewayJwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", testSecret);
        jwtTokenProvider.init();

        byte[] keyBytes = Decoders.BASE64.decode(testSecret);
        secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    @Test
    @DisplayName("유효한 JWT 토큰을 검증하면 true를 반환한다")
    void validateToken_WithValidToken_ReturnsTrue() {
        // given
        String validToken = createValidToken("test@example.com", "USER", 1L);

        // when
        boolean result = jwtTokenProvider.validateToken(validToken);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("만료된 JWT 토큰을 검증하면 false를 반환한다")
    void validateToken_WithExpiredToken_ReturnsFalse() {
        // given
        String expiredToken = createExpiredToken("test@example.com", "USER", 1L);

        // when
        boolean result = jwtTokenProvider.validateToken(expiredToken);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("잘못된 형식의 JWT 토큰을 검증하면 false를 반환한다")
    void validateToken_WithInvalidToken_ReturnsFalse() {
        // given
        String invalidToken = "invalid.token.here";

        // when
        boolean result = jwtTokenProvider.validateToken(invalidToken);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("JWT 토큰에서 userId를 올바르게 추출한다")
    void getUserIdFromJWT_WithValidToken_ReturnsUserId() {
        // given
        Long expectedUserId = 123L;
        String token = createValidToken("test@example.com", "USER", expectedUserId);

        // when
        Long actualUserId = jwtTokenProvider.getUserIdFromJWT(token);

        // then
        assertThat(actualUserId).isEqualTo(expectedUserId);
    }

    @Test
    @DisplayName("JWT 토큰에서 role을 올바르게 추출한다")
    void getRoleFromJWT_WithValidToken_ReturnsRole() {
        // given
        String expectedRole = "ADMIN";
        String token = createValidToken("test@example.com", expectedRole, 1L);

        // when
        String actualRole = jwtTokenProvider.getRoleFromJWT(token);

        // then
        assertThat(actualRole).isEqualTo(expectedRole);
    }

    @Test
    @DisplayName("다양한 role 값으로 JWT 토큰에서 role을 추출한다")
    void getRoleFromJWT_WithDifferentRoles_ReturnsCorrectRole() {
        // given
        String[] roles = {"USER", "ADMIN", "MANAGER"};

        for (String expectedRole : roles) {
            String token = createValidToken("test@example.com", expectedRole, 1L);

            // when
            String actualRole = jwtTokenProvider.getRoleFromJWT(token);

            // then
            assertThat(actualRole).isEqualTo(expectedRole);
        }
    }

    @Test
    @DisplayName("다양한 userId 값으로 JWT 토큰에서 userId를 추출한다")
    void getUserIdFromJWT_WithDifferentUserIds_ReturnsCorrectUserId() {
        // given
        Long[] userIds = {1L, 100L, 9999L};

        for (Long expectedUserId : userIds) {
            String token = createValidToken("test@example.com", "USER", expectedUserId);

            // when
            Long actualUserId = jwtTokenProvider.getUserIdFromJWT(token);

            // then
            assertThat(actualUserId).isEqualTo(expectedUserId);
        }
    }

    // Helper methods
    private String createValidToken(String username, String role, Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 3600000); // 1 hour

        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .claim("userId", userId)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    private String createExpiredToken(String username, String role, Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() - 3600000); // expired 1 hour ago

        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .claim("userId", userId)
                .issuedAt(new Date(now.getTime() - 7200000))
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }
}