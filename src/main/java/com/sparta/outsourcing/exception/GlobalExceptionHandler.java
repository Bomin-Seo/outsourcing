package com.sparta.outsourcing.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<RestApiException> illegalArgumentExceptionHandler(IllegalArgumentException ex) {
        RestApiException restApiException = new RestApiException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(
                restApiException,
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({AlreadySignupException.class})
    public ResponseEntity<RestApiException> AlreadySignupExceptionHandler(AlreadySignupException ex) {
        RestApiException restApiException = new RestApiException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(restApiException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({SignUpFailureException.class})
    public ResponseEntity<RestApiException> SignUpFailureExceptionHandler(SignUpFailureException ex) {
        RestApiException restApiException = new RestApiException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(restApiException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<RestApiException> UserNotFoundExceptionHandler(UserNotFoundException ex) {
        RestApiException restApiException = new RestApiException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(restApiException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({LikeSelfException.class})
    public ResponseEntity<RestApiException> LikeSelfExceptionHandler(LikeSelfException ex) {
        RestApiException restApiException = new RestApiException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(restApiException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NotFoundObjException.class})
    public ResponseEntity<RestApiException> NotFoundObjExceptionHandler(NotFoundObjException ex) {
        RestApiException restApiException = new RestApiException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(restApiException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({InvalidAccessException.class})
    public ResponseEntity<RestApiException> InvalidAccessExceptionHandler(InvalidAccessException ex) {
        RestApiException restApiException = new RestApiException(ex.getMessage(), HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(restApiException, HttpStatus.UNAUTHORIZED);
    }


}
