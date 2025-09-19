package com.example.librarymanagementsystem.exception;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice(basePackages = "com.example.librarymanagementsystem.controller")
@ConditionalOnProperty(prefix = "app.exception-handler", name = "enabled", havingValue = "true")
@Order(Ordered.LOWEST_PRECEDENCE) // let Spring Security handle first
public class GlobalExceptionHandler {

    private record ApiError(Instant timestamp, int status, String error, String message, String path, Object details) { }

    private ResponseEntity<ApiError> build(HttpStatus status, String message, HttpServletRequest req, Object details) {
        return ResponseEntity.status(status).body(
                new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message, req.getRequestURI(), details)
        );
    }

    // ---- 4xx you actually want ----
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuth(AuthenticationException ex, HttpServletRequest req) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), req, null); // 401
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage(), req, null); // 403
    }

    @ExceptionHandler({ NoSuchElementException.class })
    public ResponseEntity<ApiError> handleNotFound(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req, null); // 404
    }

    @ExceptionHandler({ IllegalArgumentException.class, ConstraintViolationException.class, HttpMessageNotReadableException.class })
    public ResponseEntity<ApiError> handleBadRequest(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req, null); // 400
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> fields = new HashMap<>();
        for (var err : ex.getBindingResult().getAllErrors()) {
            String field = (err instanceof FieldError fe) ? fe.getField() : err.getObjectName();
            fields.put(field, err.getDefaultMessage());
        }
        return build(HttpStatus.BAD_REQUEST, "Validation failed", req, fields); // 400
    }

    // ---- everything else => 500 (NOT 403) ----
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAny(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), req, null); // 500
    }
}
