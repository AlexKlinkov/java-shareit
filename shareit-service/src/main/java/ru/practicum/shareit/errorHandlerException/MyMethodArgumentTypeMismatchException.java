package ru.practicum.shareit.errorHandlerException;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MyMethodArgumentTypeMismatchException extends RuntimeException {
    String name;
    String value;
}
