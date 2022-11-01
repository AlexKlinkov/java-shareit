package ru.practicum.shareit.errorHandlerException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class ErrorHandler {
    // 400 - Ошибка статуса
    @ExceptionHandler(MyMethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleException(MyMethodArgumentTypeMismatchException exception) {
        Map<String, String> resp = new HashMap<>();
        resp.put("error", String.format("Unknown %s: %s", exception.getName(), exception.getValue()));
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    // 400 — если ошибка валидации: ValidationException
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleNotCorrectValidate(ValidationException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // 404 — для всех ситуаций, если искомый объект не найден
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    // 500 — если возникло исключение
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> handleServerError(RuntimeException exception) {
        return new ResponseEntity<>("Внутренняя ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
