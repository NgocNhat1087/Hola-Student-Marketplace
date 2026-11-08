package com.hsm.common;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    ResponseEntity<?> api(ApiException e) {
        return ResponseEntity.status(e.getStatus()).body(Map.of("error", e.getCode(), "message", e.getMessage()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    ResponseEntity<?> validation(Exception e) {
        return ResponseEntity.badRequest().body(Map.of("error", "VALIDATION_ERROR", "message", "Request validation failed."));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ResponseEntity<?> uploadTooLarge(MaxUploadSizeExceededException e) {
        return ResponseEntity.badRequest().body(Map.of("error", "INVALID_IMAGE", "message", "Only valid JPG or PNG images up to 5 MB are accepted."));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<?> other(Exception e) {
        log.error("Unhandled application error", e);
        return ResponseEntity.internalServerError().body(Map.of("error", "INTERNAL_ERROR", "message", "An unexpected error occurred."));
    }
}
