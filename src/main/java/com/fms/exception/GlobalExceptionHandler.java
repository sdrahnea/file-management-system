package com.fms.exception;

import com.fms.model.ErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler — converts all application exceptions into structured
 * JSON error responses instead of Spring's default whitelist error page.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleFileNotFound(FileNotFoundException ex) {
        log.warn("File not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorDTO("FILE_NOT_FOUND", ex.getMessage(), null));
    }

    @ExceptionHandler(ApiKeyUnauthorizedException.class)
    public ResponseEntity<ErrorDTO> handleUnauthorized(ApiKeyUnauthorizedException ex) {
        log.warn("Unauthorized API key access: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorDTO("UNAUTHORIZED", ex.getMessage(), null));
    }

    @ExceptionHandler(StorageQuotaExceededException.class)
    public ResponseEntity<ErrorDTO> handleQuotaExceeded(StorageQuotaExceededException ex) {
        log.warn("Storage quota exceeded: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(new ErrorDTO("STORAGE_QUOTA_EXCEEDED", ex.getMessage(), null));
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorDTO> handleRateLimit(RateLimitExceededException ex) {
        log.warn("Rate limit exceeded: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(new ErrorDTO("RATE_LIMIT_EXCEEDED", ex.getMessage(), null));
    }

    @ExceptionHandler(TenantIsNotAllowedException.class)
    public ResponseEntity<ErrorDTO> handleTenantNotAllowed(TenantIsNotAllowedException ex) {
        log.warn("Tenant not allowed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorDTO("TENANT_NOT_ALLOWED", ex.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleGeneric(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDTO("INTERNAL_ERROR", "An unexpected error occurred.", ex.getClass().getName()));
    }
}

