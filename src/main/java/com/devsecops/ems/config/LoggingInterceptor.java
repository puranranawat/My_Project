package com.devsecops.ems.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * HTTP request/response logging interceptor.
 *
 * <p>Logs every incoming request (method, URI) and outgoing response
 * (status code, processing duration in milliseconds).</p>
 */
@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTRIBUTE = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) {
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());
        log.info("Incoming request: {} {}", request.getMethod(), request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        long duration = System.currentTimeMillis() - startTime;

        log.info("Completed request: {} {} | status={} | duration={}ms",
                request.getMethod(), request.getRequestURI(),
                response.getStatus(), duration);

        if (ex != null) {
            log.error("Request resulted in exception: {} {} | error={}",
                    request.getMethod(), request.getRequestURI(), ex.getMessage());
        }
    }
}
