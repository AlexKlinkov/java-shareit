package ru.practicum.shareit.item;


import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = ItemMapper.class)
public interface ItemMapper {
    Item itemFromItemDTO (ItemDTO item);
    ItemDTO itemDTOFromItem (Item item);
}
