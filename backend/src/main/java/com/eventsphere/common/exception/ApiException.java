package com.eventsphere.common.exception;

import org.springframework.http.HttpStatus;

/**
 * TODO: extend RuntimeException, carry an HttpStatus, use from any
 * module's service layer for expected failure cases (not found, conflict...).
 */
public class ApiException extends RuntimeException {
	
	private HttpStatus  status;

	public ApiException (HttpStatus status, String message) {
		super(message);
		this.status=status;
	}
	public HttpStatus getStatus() {
	    return status;
	}
}
