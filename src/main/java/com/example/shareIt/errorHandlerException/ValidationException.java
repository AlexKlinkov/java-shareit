package com.example.shareIt.errorHandlerException;

public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    @Override
    public String getLocalizedMessage() {
        return super.getLocalizedMessage();
    }
}
