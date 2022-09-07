
package ru.practicum.shareit.request;

import org.mapstruct.Mapper;

@Mapper
public interface ItemRequestMapper {
    ItemRequest itemRequestFromItemRequestDTOInput(ItemRequestDTOInput itemRequestDTOInput, Integer userId);

    ItemRequestDTOOutput itemRequestDTOOutPutFromItemRequest(ItemRequest itemRequest, Integer userId);
}