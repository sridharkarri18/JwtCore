package com.example.JwtCore.exceptions;

import org.springframework.http.HttpStatus;

public class CustomException extends Exception {
    public CustomException(String message, HttpStatus forbidden) {
        super(message);
    }
}
