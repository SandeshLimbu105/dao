package org.texas.systembdao.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.texas.systembdao.dto.ApiError;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex,
                                                   HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND",
                ex.getMessage(), request);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> handleDuplicate(DuplicateResourceException ex,
                                                    HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, "DUPLICATE_RESOURCE",
                ex.getMessage(), request);
    }

    @ExceptionHandler(SourceUnavailableException.class)
    public ResponseEntity<ApiError> handleSourceUnavailable(SourceUnavailableException ex,
                                                            HttpServletRequest request) {
        return buildError(HttpStatus.SERVICE_UNAVAILABLE, "SOURCE_UNAVAILABLE",
                ex.getMessage(), request);
    }

    @ExceptionHandler(ConsentDeniedException.class)
    public ResponseEntity<ApiError> handleConsentDenied(ConsentDeniedException ex,
                                                        HttpServletRequest request) {
        // Use the specific reason (CONSENT_MISSING / EXPIRED / REVOKED) as the error code
        return buildError(HttpStatus.FORBIDDEN, ex.getReason(),
                ex.getMessage(), request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorized(UnauthorizedException ex,
                                                       HttpServletRequest request) {
        return buildError(HttpStatus.FORBIDDEN, "FORBIDDEN",
                ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex,
                                                     HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return buildError(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
                message, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex,
                                                  HttpServletRequest request) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                ex.getMessage() != null ? ex.getMessage() : "Unexpected error",
                request);
    }

    private ResponseEntity<ApiError> buildError(HttpStatus status, String code,
                                                String message, HttpServletRequest request) {
        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(code)
                .message(message)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(status).body(error);
    }
}