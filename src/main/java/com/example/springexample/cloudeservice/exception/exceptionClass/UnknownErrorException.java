package com.example.springexample.cloudeservice.exception.exceptionClass;

public class UnknownErrorException extends RuntimeException {
    public UnknownErrorException(String message) {
        super(message);
    }
}
