package org.example.baro.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.baro.user.entity.UserRole;
import org.example.baro.user.repository.UserRepository;
import org.example.baro.util.JwtUtil;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest httpRequest, HttpServletResponse httpResponse, FilterChain filterChain) throws IOException, ServletException {
        String bearerJwt = httpRequest.getHeader("Authorization");

        if (bearerJwt == null || !bearerJwt.startsWith("Bearer ")) {
            filterChain.doFilter(httpRequest, httpResponse);
            return;
        }

        String jwt = jwtUtil.substringToken(bearerJwt);

        try {
            Claims claims = jwtUtil.extractClaims(jwt);
            if (claims == null) {
                setErrorResponse(httpResponse, "유효하지 않은 토큰입니다.");
                return;
            }

            String subject = claims.getSubject();
            if (subject == null || subject.isEmpty()) {
                setErrorResponse(httpResponse, "토큰에 사용자 정보가 없습니다.");
                return;
            }

            Long userId;
            try {
                userId = Long.parseLong(subject);
            } catch (NumberFormatException e) {
                log.error("JWT subject를 Long으로 파싱 실패: {}", subject, e);
                setErrorResponse(httpResponse, "토큰의 사용자 ID 형식이 유효하지 않습니다.");
                return;
            }

            if(userRepository.findById(userId).isEmpty()){
                setErrorResponse(httpResponse, "존재하지 않는 사용자입니다.");
                return;
            }

            UserRole userRole = UserRole.valueOf(claims.get("userRole", String.class));

            List<SimpleGrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority("ROLE_" + userRole.name()));

            CustomAuthenticationToken authentication =
                    new CustomAuthenticationToken(userId, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(httpRequest, httpResponse);
        } catch (ExpiredJwtException e) {
            setErrorResponse(httpResponse, "토큰이 만료되었습니다.");
        } catch (Exception e) {
            setErrorResponse(httpResponse, e.getMessage());
        }
    }

    private void setErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        String body = new ObjectMapper().writeValueAsString(
                Map.of(
                        "status", 401,
                        "error", "Unauthorized",
                        "message", message
                )
        );
        response.getWriter().write(body);
    }
}
