package org.example.baro.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper; // JSON 응답을 위해 ObjectMapper 주입

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        log.warn("인증되지 않은 요청 접근 시도: {}", authException.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); // Content-Type을 application/json으로 설정
        response.setCharacterEncoding("UTF-8"); // 인코딩 설정

        // 클라이언트에게 반환할 JSON 응답 본문 생성
        String body = objectMapper.writeValueAsString(
                Map.of(
                        "status", HttpServletResponse.SC_UNAUTHORIZED,
                        "error", "Unauthorized",
                        "message", "JWT 토큰이 필요합니다." // 또는 authException.getMessage()
                )
        );
        response.getWriter().write(body);
    }
}

