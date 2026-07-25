package com.eventsphere.common.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * TODO: add @ExceptionHandler methods for ApiException, validation errors,
 * and a catch-all, each returning a consistent JSON error body.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
	
	 @ExceptionHandler(ApiException.class)
	    public ResponseEntity<Map<String, Object>> handleApiException(ApiException ex) {
	        Map<String, Object> body = new LinkedHashMap<>();
	        body.put("status", ex.getStatus().value());
	        body.put("message", ex.getMessage());
	        return ResponseEntity.status(ex.getStatus()).body(body);
	    }
	 
	 @ExceptionHandler(Exception.class)
	    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex) {
	        Map<String, Object> body = new LinkedHashMap<>();
	        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
	        body.put("message", "Something went wrong. Please try again later.");
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
	    }
}
