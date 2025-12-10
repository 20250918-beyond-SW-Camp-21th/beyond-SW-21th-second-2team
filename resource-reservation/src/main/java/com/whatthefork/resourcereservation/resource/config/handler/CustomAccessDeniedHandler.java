package com.whatthefork.resourcereservation.resource.config.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {

        // 1. 응답 상태 코드 설정 (403 Forbidden 유지)
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        // 2. 응답 콘텐츠 타입 설정 (JSON 응답을 위해)
        response.setContentType("application/json;charset=UTF-8");

        // 3. 커스텀 예외 메시지 또는 JSON 응답 작성
        // 여기서는 예시로 JSON 문자열을 직접 작성합니다.
        // 실제 프로젝트에서는 ObjectMapper를 사용하여 예외 객체를 변환하는 것이 일반적입니다.
        String jsonResponse = String.format(
                "{\"code\": \"AUTH_001\", \"message\": \"접근 권한이 부족합니다. 필요한 역할: %s\"}",
                accessDeniedException.getMessage() // 메시지를 활용하여 디버깅 정보 제공 가능
        );

        response.getWriter().write(jsonResponse);
    }
}