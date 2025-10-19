package com.example.springexample.cloudeservice.exception;

import com.example.springexample.cloudeservice.exception.exceptionClass.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> resourceNotFoundException(ResourceNotFoundException ex){
        log.error("Resource not found"+ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(NoUserException.class)
    public ResponseEntity<?> noUserException(NoUserException ex){
        log.error("No found user"+ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> userAlreadyExistsException(UserAlreadyExistsException ex){
        log.error("User already exists"+ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(ValidationErrorsException.class)
    public ResponseEntity<?> validationErrors(ValidationErrorsException ex){
        log.error("Error validation"+ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(DatabaseConnectionException.class)
    public  ResponseEntity<?> databaseConnectionException(DatabaseConnectionException ex){
        log.error("Error database connection"+ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(UnknownErrorException.class)
    public ResponseEntity<?> unknowError(UnknownErrorException ex){
        log.error("Unknown Error"+ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(UserIsNotAuthorizedException.class)
    public ResponseEntity<?> userIsNotAuthorize(UserIsNotAuthorizedException ex){
        log.error("User is not authorized"+ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

}
