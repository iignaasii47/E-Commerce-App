package com.iignaasii47.e_commerce_api.infrastructure.security;

import com.iignaasii47.e_commerce_api.infrastructure.config.RateLimitProperties;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class RateLimitingFilter extends OncePerRequestFilter {

    private final RateLimitProperties properties;
    private final ConcurrentHashMap<String, Deque<Instant>> requestTimestamps = new ConcurrentHashMap<>();

    public RateLimitingFilter(RateLimitProperties properties) {
        this.properties = properties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain)
            throws ServletException, IOException {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();
        String bucketKey = resolveBucketKey(request, path);
        int limit = resolveLimit(path);

        if (bucketKey == null || limit <= 0) {
            filterChain.doFilter(request, response);
            return;
        }

        Instant now = Instant.now();
        Deque<Instant> timestamps = requestTimestamps.computeIfAbsent(bucketKey,
                k -> new ConcurrentLinkedDeque<>());

        synchronized (timestamps) {
            Instant windowStart = now.minusSeconds(properties.getWindowSeconds());
            while (!timestamps.isEmpty() && timestamps.peekFirst().isBefore(windowStart)) {
                timestamps.pollFirst();
            }

            if (timestamps.size() >= limit) {
                long retryAfter = properties.getWindowSeconds()
                        - Duration.between(timestamps.peekFirst(), now).getSeconds();
                response.setStatus(429);
                response.setHeader("Retry-After", String.valueOf(retryAfter));
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Too many requests. Please try again later.\"}");
                return;
            }

            timestamps.addLast(now);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveBucketKey(HttpServletRequest request, String path) {
        if (path.equals(properties.getLoginPath())) {
            return "LOGIN:" + request.getRemoteAddr();
        }
        if (path.equals(properties.getRegistrationPath())) {
            return "REGISTRATION:" + request.getRemoteAddr();
        }
        if (path.equals(properties.getRefreshPath())) {
            return "REFRESH:" + request.getRemoteAddr();
        }
        if (path.equals(properties.getChatPath())) {
            return "CHAT:" + resolveChatKey(request);
        }
        return null;
    }

    private String resolveChatKey(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Long userId) {
            return "user:" + userId;
        }
        return request.getRemoteAddr();
    }

    private int resolveLimit(String path) {
        if (path.equals(properties.getLoginPath())) {
            return properties.getLogin();
        }
        if (path.equals(properties.getRegistrationPath())) {
            return properties.getRegistration();
        }
        if (path.equals(properties.getRefreshPath())) {
            return properties.getTokenRefresh();
        }
        if (path.equals(properties.getChatPath())) {
            return properties.getChat();
        }
        return 0;
    }

}
