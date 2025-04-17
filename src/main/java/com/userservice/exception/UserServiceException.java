package com.userservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class UserServiceException {

    @ExceptionHandler(ProductException.class)
    public ResponseEntity<String> handleCustNoFountException(ProductException ex){
        log.debug("Error occured , Test ProductException thrown");
       return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<String> handleUserExceptionException(UserException ex){
        log.debug("Error occured , UserException thrown");
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
