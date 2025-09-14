package com.example.springexample.cloudeservice.exception.exceptionClass;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException(String message){
        super(message);
    }
}
