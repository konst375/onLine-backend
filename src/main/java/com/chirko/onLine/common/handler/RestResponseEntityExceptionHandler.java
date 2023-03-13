package com.chirko.onLine.common.handler;

import com.chirko.onLine.secure.registration.exception.UserAlreadyExitsException;
import com.chirko.onLine.secure.token.commonToken.exception.CommonTokenExpiredException;
import com.chirko.onLine.secure.token.commonToken.exception.CommonTokenForSuchUserNotFoundException;
import com.chirko.onLine.secure.token.commonToken.exception.InvalidCommonTokenException;
import com.chirko.onLine.user.exception.InvalidOldPasswordException;
import com.chirko.onLine.user.exception.UserEmailNotFoundException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        return handleExceptionInternal(ex, ex.getMessage(), headers, status, request);
    }

    @ExceptionHandler(value = {UserAlreadyExitsException.class})
    protected ResponseEntity<Object> handleUserAlreadyExitsException(UserAlreadyExitsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {UserEmailNotFoundException.class})
    protected ResponseEntity<Object> handleUserEmailNotFoundException(UserEmailNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {InvalidOldPasswordException.class})
    protected ResponseEntity<Object> handleUserEmailNotFoundException(InvalidOldPasswordException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {ExpiredJwtException.class})
    protected ResponseEntity<Object> handleTokenExpiredException(ExpiredJwtException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {CommonTokenExpiredException.class})
    protected ResponseEntity<Object> handleTokenExpiredException(CommonTokenExpiredException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {CommonTokenForSuchUserNotFoundException.class})
    protected ResponseEntity<Object> handleTokenExpiredException(CommonTokenForSuchUserNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {InvalidCommonTokenException.class})
    protected ResponseEntity<Object> handleTokenExpiredException(InvalidCommonTokenException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
