package com.example.springexample.cloudeservice.exception.exceptionClass;

public class UserIsNotAuthorizedException extends RuntimeException{
    public UserIsNotAuthorizedException(String message) {
        super(message);
    }
}
