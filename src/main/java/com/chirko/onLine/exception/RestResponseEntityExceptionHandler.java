package com.chirko.onLine.exception;

import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.Timestamp;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = {OnLineException.class})
    protected ResponseEntity<Object> handleOnLineException(OnLineException ex) {
        ResponseException response = new ResponseException(ex.getMessage(), ex.getErrorCause(),
                new Timestamp(System.currentTimeMillis()));
        return new ResponseEntity<>(response, ex.getHttpStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        return handleExceptionInternal(ex, ex.getMessage(), headers, status, request);
    }

//    @ExceptionHandler(value = {Exception.class})
//    protected ResponseEntity<Object> handleException(Exception ex) {
//        if (ex instanceof ExpiredJwtException) {
//            return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
//        } else if (ex instanceof BadCredentialsException) {
//            return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
//        } else {
//            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
}
