package com.nivakaran.bookingservice.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
    }

    public ResourceNotFoundException(String resourceName, Object fieldValue) {
        super(String.format("%s not found: '%s'", resourceName, fieldValue));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}