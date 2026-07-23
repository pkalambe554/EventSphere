package com.eventsphere.common.exception;

/**
 * TODO: extend RuntimeException, carry an HttpStatus, use from any
 * module's service layer for expected failure cases (not found, conflict...).
 */
public class ApiException extends RuntimeException {
}
