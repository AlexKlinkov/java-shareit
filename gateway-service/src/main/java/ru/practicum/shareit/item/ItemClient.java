package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDTOInput;
import ru.practicum.shareit.item.dto.ItemDTOInput;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    // Создание вещи
    public ResponseEntity<Object> createItem(Long ownerId, ItemDTOInput item) {
        return post("", ownerId, item);
    }

    // Обновление вещи
    public ResponseEntity<Object> updateItem(Long ownerId, Long itemId, ItemDTOInput item) {
        return patch("/" + itemId, ownerId, item);
    }

    // Возвращает все вещи владельца
    public ResponseEntity<Object> getItemsOfUser(Long ownerId) {
        return get("", ownerId);
    }

    // Возвращает вещь по ее ID
    public ResponseEntity<Object> getItemById(Long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    // Возвращает вещь по тексту
    public ResponseEntity<Object> getItemBySearchText(String text) {
        String param = "/search?text=" + text;
        return get(param);
    }

    // Создаем комментарий
    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentDTOInput commentDTOInput) {
        return post("/" + itemId + "/comment", userId, commentDTOInput);
    }
}
