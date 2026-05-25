package com.akshansh.timecapsulebackend.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Order(1)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        long start = System.currentTimeMillis();

        // requestId so every log line in this thread carries it
        MDC.put("requestId", requestId);

        log.info("IN  method={} uri={} ip={}",
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr());

        filterChain.doFilter(request, response);

        log.info("OUT status={} uri={} duration={}ms",
                response.getStatus(),
                request.getRequestURI(),
                System.currentTimeMillis() - start);

        try {
            filterChain.doFilter(request, response); // JwtAuthFilter runs here, SecurityContext gets populated
        } finally {
            // OUT — logged after full chain, userId now available
            String userId = extractUserIdFromSecurityContext();
            log.info("OUT method={} uri={} status={} userId={} duration={}ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    userId,
                    System.currentTimeMillis() - start);
            MDC.clear();
        }
    }

    private String extractUserIdFromSecurityContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return "anonymous";
        return auth.getName();
    }
}
