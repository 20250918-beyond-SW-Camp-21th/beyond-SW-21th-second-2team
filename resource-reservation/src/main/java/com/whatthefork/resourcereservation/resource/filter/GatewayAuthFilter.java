package com.whatthefork.resourcereservation.resource.filter;

import com.whatthefork.resourcereservation.resource.dto.user.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
public class GatewayAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        // swagger 필터 스킵
        if (uri.startsWith("/v3/api-docs")
                || uri.startsWith("/swagger-ui")
                || uri.startsWith("/swagger-resources")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String userId = request.getHeader("X-User-ID");
        final String userRoles = request.getHeader("X-User-Role");

        log.info("User {} has roles {}", userId, userRoles);

        if (userId != null && userRoles != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            List<GrantedAuthority> authorities = Arrays.stream(userRoles.split(","))
                    .map(String::trim)
                    .map(role -> "ROLE_" + role.toUpperCase())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            UserDetails userDetails = new UserDetailsImpl(userId, authorities);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, // Principal: UserDetailsImpl 객체
                    null,        // Credentials: null (이미 게이트웨이에서 인증 완료)
                    authorities  // Authorities: 권한 목록
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("userId = {}, userRoles = {}", userId, userRoles);
        }

        filterChain.doFilter(request, response);
    }
}