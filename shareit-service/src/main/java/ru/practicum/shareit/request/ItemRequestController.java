package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/requests")
public class ItemRequestController {

    private final ServiceItemRequest serviceItemRequest;

    @Autowired
    public ItemRequestController(@Qualifier("ServiceItemRequestInBD") ServiceItemRequest serviceItemRequest) {
        this.serviceItemRequest = serviceItemRequest;
    }

    @PostMapping
    public ItemRequestDTOOutput create(@RequestHeader(value = "X-Sharer-User-Id") int userId,
                                       @RequestBody @Valid ItemRequestDTOInput itemRequestDTOInput) {
        return serviceItemRequest.create(userId, itemRequestDTOInput);
    }

    // Метод возвращающий все запросы на вещи созданные одним пользователем
    @GetMapping
    public List<ItemRequestDTOOutput> getItemRequestsByUserId(@RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return serviceItemRequest.getItemRequestsByUserId(userId);
    }

    // Метод возвращабщий конкретный запрос на вещь конкретного пользователя
    @GetMapping(path = "/{requestId}")
    public ItemRequestDTOOutput getRequestByRequestIdAndUserId(@RequestHeader(value = "X-Sharer-User-Id") int userId,
                                                               @PathVariable int requestId) {
        return serviceItemRequest.getRequestByRequestIdAndUserId(userId, requestId);
    }

    // Метод возвращающий абсолютно все запросы на вещи по странично
    @GetMapping("/all")
    public List<ItemRequestDTOOutput> getItemRequestsOfUser(@RequestHeader(value = "X-Sharer-User-Id") int userId,
                                                            @RequestParam(value = "from", required = false,
                                                                    defaultValue = "0") Integer from,
                                                            @RequestParam(value = "size", required = false,
                                                                    defaultValue = "5") Integer size,
                                                            @RequestParam(value = "sort", required = false,
                                                                    defaultValue = "id") String sort) {
        return serviceItemRequest.getItemRequestsOfUser(userId, from, size, sort);
    }
}