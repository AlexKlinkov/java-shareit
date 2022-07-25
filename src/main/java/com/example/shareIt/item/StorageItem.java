package com.example.shareIt.item;

import java.util.List;

public interface StorageItem {
    ItemDTO create (int ownerId, ItemDTO item);
    ItemDTO update (int ownerId, int itemId, ItemDTO item);
    List<ItemDTO> getItems (int ownerId);
    ItemDTO getItemById (int itemId);
    List<ItemDTO> getItemBySearchText (String text);
}
