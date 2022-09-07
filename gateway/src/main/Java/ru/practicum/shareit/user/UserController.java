package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDTOInput;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    // Создание пользователя
    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserDTOInput userDTOInput) {
        log.info("Creating user={}", userDTOInput);
        return userClient.createUser(userDTOInput);
    }

    // Обновление пользователя по ID
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable @PositiveOrZero Long userId,
                                             @RequestBody @Valid UserDTOInput userDTOInput) {
        log.info("Updating userId={}", userId);
        return userClient.updateUser(userId, userDTOInput);
    }

    // Получение всех пользователей
    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Getting all users");
        return userClient.getUsers();
    }

    // Получение пользователя по ID
    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable @PositiveOrZero Long userId) {
        log.info("Getting user by ID");
        return userClient.getUserById(userId);
    }

    // Удаление пользователя по ID
    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUserById(@PathVariable @PositiveOrZero Long userId) {
        log.info("Delete user by ID");
        return userClient.deleteUserById(userId);
    }
}
