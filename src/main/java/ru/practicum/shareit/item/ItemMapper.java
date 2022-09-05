package ru.practicum.shareit.item;

import org.mapstruct.Mapper;

@Mapper
public interface ItemMapper {
    Item itemFromItemDTO(ItemDTO item);

    ItemDTO itemDTOFromItem(Item item);
}
