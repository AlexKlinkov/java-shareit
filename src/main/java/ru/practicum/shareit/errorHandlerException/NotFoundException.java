package ru.practicum.shareit.errorHandlerException;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

}
