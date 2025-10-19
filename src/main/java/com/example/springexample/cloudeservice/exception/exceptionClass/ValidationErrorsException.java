package com.example.springexample.cloudeservice.exception.exceptionClass;

public class ValidationErrorsException extends RuntimeException {
    public ValidationErrorsException(String message) {
        super(message);
    }
}
