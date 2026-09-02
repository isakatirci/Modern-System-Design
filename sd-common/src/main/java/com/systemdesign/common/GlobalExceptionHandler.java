package com.systemdesign.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler; tüm controller'lardan fırlayan hataları
 * RFC 7807 {@code ProblemDetail} formatında standart response'a çevirir.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Geçersiz argüman hatalarını HTTP 400 Bad Request olarak döner.
     *
     * @param ex fırlatılan {@code IllegalArgumentException}
     * @return ProblemDetail response body
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        detail.setTitle("Invalid request");
        return detail;
    }

    /**
     * Geçersiz state hatalarını HTTP 409 Conflict olarak döner.
     *
     * @param ex fırlatılan {@code IllegalStateException}
     * @return ProblemDetail response body
     */
    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalState(IllegalStateException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        detail.setTitle("Conflict");
        return detail;
    }

    /**
     * Bean validation hatalarını HTTP 400 Bad Request olarak döner.
     *
     * @param ex {@code @Valid} annotation ile tetiklenen validation exception
     * @return ilk field hatasını içeren ProblemDetail response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
        detail.setTitle("Validation error");
        return detail;
    }
}
