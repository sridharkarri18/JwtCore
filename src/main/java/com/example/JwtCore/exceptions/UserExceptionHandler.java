package com.example.JwtCore.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(UserDefinedException.class)
    public ResponseEntity message(UserDefinedException exception) {
        Map<String, Object> map = new LinkedHashMap();
        map.put("message", exception.getMessage());
        map.put("status", HttpStatus.BAD_REQUEST.value());
        map.put("timestamp", Instant.now());
        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(CustomException.class)
    public ResponseEntity message(CustomException exception) {
        Map<String, Object> map = new LinkedHashMap();
        map.put("message", exception.getMessage());
        map.put("status", HttpStatus.BAD_REQUEST.value());
        map.put("timestamp", Instant.now());
        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(TokenException.class)
    public ResponseEntity message(TokenException exception) {
        Map<String, Object> map = new LinkedHashMap();
        map.put("message", exception.getMessage());
        map.put("status", HttpStatus.BAD_REQUEST.value());
        map.put("timestamp", Instant.now());
        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationErrors(MethodArgumentNotValidException e) {
        Map<String, String> error = new HashMap<>();
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        allErrors.forEach(err -> {FieldError fieldError = (FieldError) err;
            error.put(fieldError.getField(), fieldError.getDefaultMessage());
        });
        return error;
    }
}
