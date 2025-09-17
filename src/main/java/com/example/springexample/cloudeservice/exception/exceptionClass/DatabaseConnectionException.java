package com.example.springexample.cloudeservice.exception.exceptionClass;

public class DatabaseConnectionException extends RuntimeException {
    public DatabaseConnectionException(String message) {
        super(message);
    }
}
