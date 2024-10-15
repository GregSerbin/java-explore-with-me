package ru.practicum.errorHandler;

public class RestrictionsViolationException extends RuntimeException {
    public RestrictionsViolationException(String message) {
        super(message);
    }
}
