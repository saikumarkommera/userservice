package com.userservice.exception;

import com.userservice.exception.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class UserServiceException {

    @ExceptionHandler
    public ResponseEntity<String> handleCustNoFountException(ProductException ex){
       return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
