package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.RequestDTOInput;

import java.util.Map;

@Service
public class RequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    // Создание запроса на вещь от пользователя
    public ResponseEntity<Object> createRequest(Long userId, RequestDTOInput requestDTOInput) {
        return post("", userId, requestDTOInput);
    }

    // Контроллер возвращающий все запросы на вещи созданные одним пользователем
    public ResponseEntity<Object> getItemRequestsByUserId(Long userId) {
        return get("", userId);
    }

    // Контроллер возвращабщий конкретный запрос на вещь конкретного пользователя
    public ResponseEntity<Object> getRequestByRequestIdAndUserId(Long requestId, Long userId) {
        return get("/" + requestId, userId);
    }

    // Метод возвращающий абсолютно все запросы на вещи по странично
    public ResponseEntity<Object> getItemRequestsOfUser(long userId, Integer from,
                                                        Integer size) {

        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all?from={from}&size={size}", userId, parameters);
    }
}
