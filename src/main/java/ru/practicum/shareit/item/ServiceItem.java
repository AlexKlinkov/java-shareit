package ru.practicum.shareit.item;

import java.util.List;

public interface ServiceItem {
    ItemDTO create (int ownerId, ItemDTO item);
    ItemDTO update (int ownerId, int itemId, ItemDTO item);
    List<ItemDTO> getItems (int ownerId);
    ItemDTO getItemById (int itemId);
    List<ItemDTO> getItemBySearchText (String text);
}
