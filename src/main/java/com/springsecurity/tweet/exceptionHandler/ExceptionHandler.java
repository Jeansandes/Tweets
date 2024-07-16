package com.springsecurity.tweet.exceptionHandler;

import com.springsecurity.tweet.Exceptions.ForbiddenException;
import com.springsecurity.tweet.Exceptions.UserAlreadyExistsException;
import com.springsecurity.tweet.Exceptions.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ExceptionHandler extends RuntimeException{
    @org.springframework.web.bind.annotation.ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<StandardError> BadCredentialsException(BadCredentialsException ex){
        StandardError err = new StandardError(ex.getMessage());
        return ResponseEntity.unprocessableEntity().body(err);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<StandardError> UserAlreadyExistsException(UserAlreadyExistsException ex){
        StandardError err = new StandardError(ex.getMessage());
        return ResponseEntity.unprocessableEntity().body(err);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ForbiddenException.class)
    public  ResponseEntity<StandardError> ForbiddenException(ForbiddenException ex) {
        StandardError err = new StandardError(ex.getMessage());
        return ResponseEntity.notFound().build();
    }
    @org.springframework.web.bind.annotation.ExceptionHandler(UserNotFoundException.class)
    public  ResponseEntity<StandardError> UserNotFoundException(UserNotFoundException ex) {
        StandardError err = new StandardError(ex.getMessage());
        return ResponseEntity.notFound().build();
    }
}



















