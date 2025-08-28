package com.zandome.syncplaylist.config;

import com.zandome.syncplaylist.shared.domain.exceptions.BaseException;
import com.zandome.syncplaylist.shared.domain.exceptions.JwtException;
import com.zandome.syncplaylist.shared.domain.exceptions.ValidationException;
import com.zandome.syncplaylist.shared.infra.http.dtos.ErrorResponse;
import com.zandome.syncplaylist.user.domain.exceptions.InvalidCredentialsException;
import com.zandome.syncplaylist.user.domain.exceptions.UserAlreadyExistsException;
import com.zandome.syncplaylist.user.domain.exceptions.UserNotFoundException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.TransactionException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

        // ========================================
        // UNIVERSAL CATCH-ALL EXCEPTION HANDLER
        // ========================================

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGenericException(
                        Exception ex, HttpServletRequest request) {
                return handleAnyException(ex, request);
        }

        // ========================================
        // UNIVERSAL EXCEPTION HANDLER
        // ========================================

        /**
         * Universal exception handler that can catch ANY type of exception
         * This method analyzes the exception type and provides appropriate handling
         */
        public ResponseEntity<ErrorResponse> handleAnyException(
                        Throwable throwable, HttpServletRequest request) {

                String traceId = generateTraceId();
                String exceptionType = throwable.getClass().getSimpleName();
                String message = throwable.getMessage();

                // Log with full context
                log.error("Exception caught [{}] - Type: {}, Message: {}",
                                traceId, exceptionType, message, throwable);

                // Determine appropriate response based on exception characteristics
                ExceptionInfo exceptionInfo = analyzeException(throwable);

                // Build additional context details
                Map<String, Object> details = buildExceptionDetails(throwable, request);

                return buildErrorResponse(
                                exceptionInfo.userMessage(),
                                exceptionInfo.code(),
                                exceptionInfo.status(),
                                request,
                                traceId,
                                details);
        }

        /**
         * Analyzes any exception and determines the appropriate response
         */
        private ExceptionInfo analyzeException(Throwable throwable) {
                String className = throwable.getClass().getName().toLowerCase();
                String message = throwable.getMessage() != null ? throwable.getMessage().toLowerCase() : "";

                // Security-related exceptions
                if (className.contains("security") || className.contains("auth") ||
                                className.contains("credential") || className.contains("permission")) {
                        return new ExceptionInfo(
                                        "Authentication or authorization failed",
                                        "SECURITY_ERROR",
                                        HttpStatus.UNAUTHORIZED);
                }

                // Database/Persistence exceptions
                if (className.contains("sql") || className.contains("database") ||
                                className.contains("persistence") || className.contains("hibernate") ||
                                className.contains("jpa") || className.contains("mongo")) {
                        return new ExceptionInfo(
                                        "Database operation failed",
                                        "DATABASE_ERROR",
                                        HttpStatus.INTERNAL_SERVER_ERROR);
                }

                // Validation exceptions
                if (className.contains("validation") || className.contains("constraint") ||
                                message.contains("validation") || message.contains("invalid")) {
                        return new ExceptionInfo(
                                        "Validation failed",
                                        "VALIDATION_ERROR",
                                        HttpStatus.BAD_REQUEST);
                }

                // Network/IO exceptions
                if (className.contains("io") || className.contains("network") ||
                                className.contains("timeout") || className.contains("connection")) {
                        return new ExceptionInfo(
                                        "Network or I/O error occurred",
                                        "IO_ERROR",
                                        HttpStatus.SERVICE_UNAVAILABLE);
                }

                // JSON/Serialization exceptions
                if (className.contains("json") || className.contains("jackson") ||
                                className.contains("serialization") || className.contains("parse")) {
                        return new ExceptionInfo(
                                        "Data format error",
                                        "SERIALIZATION_ERROR",
                                        HttpStatus.BAD_REQUEST);
                }

                // HTTP-related exceptions
                if (className.contains("http") || className.contains("web") ||
                                className.contains("servlet") || className.contains("request")) {
                        return new ExceptionInfo(
                                        "HTTP request error",
                                        "HTTP_ERROR",
                                        HttpStatus.BAD_REQUEST);
                }

                // Client errors (4xx)
                if (message.contains("not found") || message.contains("missing") ||
                                message.contains("required") || message.contains("invalid")) {
                        return new ExceptionInfo(
                                        "Invalid request",
                                        "CLIENT_ERROR",
                                        HttpStatus.BAD_REQUEST);
                }

                // Configuration/Setup errors
                if (className.contains("config") || className.contains("bean") ||
                                className.contains("initialization") || message.contains("configuration")) {
                        return new ExceptionInfo(
                                        "System configuration error",
                                        "CONFIGURATION_ERROR",
                                        HttpStatus.INTERNAL_SERVER_ERROR);
                }

                // Default fallback
                return new ExceptionInfo(
                                "An unexpected error occurred",
                                "UNKNOWN_ERROR",
                                HttpStatus.INTERNAL_SERVER_ERROR);
        }

        /**
         * Builds detailed information about the exception for debugging
         */
        private Map<String, Object> buildExceptionDetails(Throwable throwable, HttpServletRequest request) {
                Map<String, Object> details = new HashMap<>();

                details.put("exceptionType", throwable.getClass().getName());
                details.put("exceptionMessage", throwable.getMessage());
                details.put("requestMethod", request.getMethod());
                details.put("requestURL", request.getRequestURL().toString());
                details.put("userAgent", request.getHeader("User-Agent"));
                details.put("clientIP", getClientIP(request));

                // Add stack trace for server errors (careful not to expose in production)
                if (throwable instanceof RuntimeException || throwable instanceof Error) {
                        details.put("hasStackTrace", true);
                }

                // Add cause information if available
                if (throwable.getCause() != null) {
                        details.put("causeType", throwable.getCause().getClass().getName());
                        details.put("causeMessage", throwable.getCause().getMessage());
                }

                // Add specific details based on exception type
                addSpecificExceptionDetails(throwable, details);

                return details;
        }

        /**
         * Adds specific details based on the exception type
         */
        private void addSpecificExceptionDetails(Throwable throwable, Map<String, Object> details) {
                // Add validation details for validation exceptions
                if (throwable instanceof ConstraintViolationException cve) {
                        details.put("violations", cve.getConstraintViolations().size());
                }

                // Add SQL details for database exceptions
                if (throwable.getClass().getName().contains("SQL")) {
                        details.put("sqlError", true);
                }

                // Add more specific details as needed
                details.put("timestamp", System.currentTimeMillis());
        }

        /**
         * Record to hold exception analysis results
         */
        private record ExceptionInfo(String userMessage, String code, HttpStatus status) {
        }

        /**
         * Method to handle any exception type - can be called directly
         */
        public ResponseEntity<ErrorResponse> catchAllExceptions(
                        Object exception, HttpServletRequest request) {

                if (exception instanceof Throwable throwable) {
                        return handleAnyException(throwable, request);
                }

                // If somehow a non-throwable object is passed
                String traceId = generateTraceId();
                log.error("Non-throwable object passed to exception handler [{}]: {}",
                                traceId, exception.getClass().getName());

                return buildErrorResponse(
                                "An unexpected error occurred",
                                "UNKNOWN_OBJECT_ERROR",
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                request,
                                traceId,
                                Map.of("objectType", exception.getClass().getName()));
        }

        // ========================================
        // UTILITY METHODS
        // ========================================

        private ResponseEntity<ErrorResponse> buildErrorResponse(
                        String message, String code, HttpStatus status,
                        HttpServletRequest request, String traceId, Map<String, Object> details) {

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .message(message)
                                .code(code)
                                .path(request.getRequestURI())
                                .timestamp(LocalDateTime.now())
                                .status(status.value())
                                .error(status.getReasonPhrase())
                                .details(details)
                                .traceId(traceId)
                                .build();

                return ResponseEntity.status(status).body(errorResponse);
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