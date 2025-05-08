package org.example.icatch.config;

import org.example.icatch.User.User;
import org.example.icatch.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SurveyInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 현재 인증된 사용자 정보 가져오기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            String email = auth.getName();
            User user = userService.getUserByEmail(email);

            // 설문조사가 필요한 사용자인지 확인
            if (user != null && !user.isSurveyCompleted()) {
                // API 요청이 아닌 경우에만 리다이렉트
                if (!request.getRequestURI().startsWith("/api/")) {
                    response.setHeader("X-Needs-Survey", "true");
                }
            }
        }

        return true;
    }
}