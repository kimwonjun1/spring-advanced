package org.example.expert.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.enums.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AdminInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        AuthUser authUser = (AuthUser) request.getAttribute("authUser");

        if (authUser == null || authUser.getUserRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("관리자 권한이 필요합니다.");
        }

        logger.info("Admin Access, UserId: {}, Time: {}, URL: {}",
                authUser.getId(),
                LocalDateTime.now(),
                request.getRequestURI());

        return true;
    }
}
