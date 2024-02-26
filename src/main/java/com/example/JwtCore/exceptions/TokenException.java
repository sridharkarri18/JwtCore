package com.example.JwtCore.exceptions;

public class TokenException extends RuntimeException{
    public TokenException(String message) {
        super(message);
    }
}
