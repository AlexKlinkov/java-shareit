package ru.practicum.shareit.request;

import java.util.List;


public interface ServiceItemRequest {
    ItemRequestDTOOutput create(int userId, ItemRequestDTOInput itemRequestDTOInput);

    List<ItemRequestDTOOutput> getItemRequestsOfUser(int userId, Integer from, Integer size, String sort);

    ItemRequestDTOOutput getRequestByRequestIdAndUserId(int userId, int requestId);

    List<ItemRequestDTOOutput> getItemRequestsByUserId(int userId);
}