package com.bslecommerce.springbackend.util;

import com.bslecommerce.springbackend.util.Response.ResponseMaker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        if (ex instanceof HttpRequestMethodNotSupportedException reqEx) {
            httpStatus = (HttpStatus) reqEx.getStatusCode();
        }
        return ResponseEntity.status(httpStatus).body(ResponseMaker.errorRes(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMismatchException(MethodArgumentTypeMismatchException ex) {
        String attributeName = ex.getName();
        int idIndex = attributeName.indexOf("Id");
        String modelName = attributeName.substring(0, idIndex);
        modelName = modelName.substring(0, 1).toUpperCase() + modelName.substring(1);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes(modelName + " not found"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String,String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String fieldError = error.getDefaultMessage();
            errors.put(fieldName, fieldError);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMaker.errorRes("Validation Error", errors));
    }
}
