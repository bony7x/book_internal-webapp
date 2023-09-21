package com.example.auth.exception;

public class UserConflictException extends Exception{

    public UserConflictException(String message){
        super(message);
    }
}
