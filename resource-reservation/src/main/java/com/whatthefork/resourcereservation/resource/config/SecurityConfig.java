package com.whatthefork.resourcereservation.resource.config;

import com.whatthefork.resourcereservation.resource.config.handler.CustomAccessDeniedHandler;
import com.whatthefork.resourcereservation.resource.filter.GatewayAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@Configuration
@Component
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // @PreAuthorize 활성화
public class SecurityConfig {

    private final GatewayAuthFilter gatewayAuthFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    public SecurityConfig(GatewayAuthFilter gatewayAuthFilter, CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.gatewayAuthFilter = gatewayAuthFilter;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    // 2. 필터 체인 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화 (API 서버이므로)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 미사용
                        .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/conference-rooms/**", "/corporate-cars/**", "/supplies/**", "/reservations/**").permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-resources/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                        )
                .addFilterBefore(gatewayAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(handling -> {
                    handling.accessDeniedHandler(customAccessDeniedHandler);
                });

        // 3. 커스텀 필터를 UsernamePasswordAuthenticationFilter 이전에 추가
//        http.addFilterBefore(new com.whatthefork.resourcereservation.resource.filter.GatewayAuthFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}