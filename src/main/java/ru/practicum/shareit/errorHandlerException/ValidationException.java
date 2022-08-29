package ru.practicum.shareit.errorHandlerException;

public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

}
