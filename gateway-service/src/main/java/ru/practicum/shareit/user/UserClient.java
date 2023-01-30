package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDTOInput;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    // Создание пользователя
    public ResponseEntity<Object> createUser(UserDTOInput userDTOInput) {
        return post("", userDTOInput);
    }

    // Обновление пользователя
    public ResponseEntity<Object> updateUser(Long userId, UserDTOInput userDTOInput) {
        return patch("/" + userId, userDTOInput);
    }

    // Получение всех пользователей
    public ResponseEntity<Object> getUsers() {
        return get("");
    }

    // Получение пользователя по ID
    public ResponseEntity<Object> getUserById(Long userId) {
        return get("/" + userId);
    }

    // Удаление пользователя по ID
    public ResponseEntity<Object> deleteUserById(Long userId) {
        return delete("/" + userId);
    }
}
