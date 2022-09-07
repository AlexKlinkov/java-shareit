package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDTOInput;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
private final RequestClient requestClient;

    // Создание запроса на вещь от пользователя
    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(value = "X-Sharer-User-Id") @PositiveOrZero Long userId,
                                               @RequestBody @Valid RequestDTOInput requestDTOInput) {
        log.info("Creating request={}, by userid={}", requestDTOInput, userId);
        return requestClient.createRequest(userId, requestDTOInput);
    }

    // Контроллер возвращающий все запросы на вещи созданные одним пользователем
    @GetMapping
    public ResponseEntity<Object> getItemRequestsByUserId(@RequestHeader(value = "X-Sharer-User-Id")
                                                               @PositiveOrZero Long userId) {
        log.info("Getting all requests, by userid={}", userId);
        return requestClient.getItemRequestsByUserId(userId);
    }

    // Контроллер возвращабщий конкретный запрос на вещь конкретного пользователя
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestByRequestIdAndUserId(@PathVariable @PositiveOrZero Long requestId,
                                                                 @RequestHeader(value = "X-Sharer-User-Id")
                                                                 @PositiveOrZero Long userId) {
        log.info("Getting requestId={}, by userid={}", requestId, userId);
        return requestClient.getRequestByRequestIdAndUserId(requestId, userId);
    }

    // Метод возвращающий абсолютно все запросы на вещи по странично
    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequestsOfUser(@RequestHeader(value = "X-Sharer-User-Id")
                                                        @PositiveOrZero Long userId,
                                                        @RequestParam(value = "from", required = false,
                                                                defaultValue = "0") Integer from,
                                                        @RequestParam(value = "size", required = false,
                                                                defaultValue = "5") Integer size,
                                                        @RequestParam(value = "sort", required = false,
                                                                defaultValue = "id") String sort) {
        log.info("Getting all requestId from={}, size={}, sort={} by userid={}", from, size, sort, userId);
        return requestClient.getItemRequestsOfUser(userId, from, size, sort);
    }
}
