package org.example.expert.config.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.expert.domain.common.dto.AuthUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
public class AdminApiLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(AdminApiLoggingAspect.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Around("execution(* org.example.expert.domain.comment.controller.CommentAdminController.*(..)) || " +
            "execution(* org.example.expert.domain.user.controller.UserAdminController.*(..))")
    public Object logAdminApi(ProceedingJoinPoint joinPoint) throws Throwable {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        AuthUser authUser = (AuthUser) request.getAttribute("authUser");

        if (authUser == null) {
            logger.warn("Admin API Request, authUser not found in request attribute. Check filter chain or Interceptor.");
        }

        String userId = authUser != null ? String.valueOf(authUser.getId()) : "UNKNOWN";
        String url = request.getRequestURI();
        String time = LocalDateTime.now().toString();
        String requestBody = extractRequestBody(joinPoint);

        logger.info("Admin API Request, UserId: {}, Time: {}, URL: {}, RequestBody: {}",
                userId, time, url, requestBody);

        Object response = joinPoint.proceed();

        String responseBody = objectMapper.writeValueAsString(response);
        logger.info("Admin API Response, UserId: {}, Time: {}, URL: {}, ResponseBody: {}",
                userId, time, url, responseBody);

        return response;
    }

    private String extractRequestBody(ProceedingJoinPoint joinPoint) {

        try {
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (arg != null && !(arg instanceof HttpServletRequest) && !(arg instanceof HttpServletResponse)) {
                    return objectMapper.writeValueAsString(arg);
                }
            }
            return "No body";
        } catch (Exception e) {
            return "Failed to parse request body";
        }
    }
}
