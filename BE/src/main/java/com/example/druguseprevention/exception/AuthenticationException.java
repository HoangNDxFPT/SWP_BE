package com.example.druguseprevention.exception;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException (String message){
        super(message);
    }
}
