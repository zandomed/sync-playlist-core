package com.zandome.syncplaylist.config;

import com.zandome.syncplaylist.shared.infra.http.dtos.ErrorResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleAllExceptions(
                        Exception ex, HttpServletRequest request) {

                String traceId = generateTraceId();

                log.error("Exception caught [{}] - Type: {}, Message: {}",
                                traceId, ex.getClass().getSimpleName(), ex.getMessage(), ex);

                Map<String, Object> details = buildExceptionDetails(ex, request);

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .message("An unexpected error occurred")
                                .code("INTERNAL_SERVER_ERROR")
                                .path(request.getRequestURI())
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                                .details(details)
                                .traceId(traceId)
                                .build();

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }

        private Map<String, Object> buildExceptionDetails(Exception ex, HttpServletRequest request) {
                Map<String, Object> details = new HashMap<>();

                details.put("exceptionType", ex.getClass().getName());
                details.put("exceptionMessage", ex.getMessage());
                details.put("requestMethod", request.getMethod());
                details.put("requestURL", request.getRequestURL().toString());
                details.put("clientIP", getClientIP(request));
                details.put("timestamp", System.currentTimeMillis());

                if (ex.getCause() != null) {
                        details.put("causeType", ex.getCause().getClass().getName());
                        details.put("causeMessage", ex.getCause().getMessage());
                }

                return details;
        }

        private String generateTraceId() {
                return UUID.randomUUID().toString().substring(0, 8);
        }

        private String getClientIP(HttpServletRequest request) {
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                        return xForwardedFor.split(",")[0].trim();
                }
                String xRealIp = request.getHeader("X-Real-IP");
                if (xRealIp != null && !xRealIp.isEmpty()) {
                        return xRealIp;
                }
                return request.getRemoteAddr();
        }
}