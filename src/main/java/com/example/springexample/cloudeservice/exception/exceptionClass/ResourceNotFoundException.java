package com.example.springexample.cloudeservice.exception.exceptionClass;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
