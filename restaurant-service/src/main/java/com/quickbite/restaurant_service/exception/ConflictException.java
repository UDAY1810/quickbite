package com.quickbite.restaurant_service.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) { super(message); }
}