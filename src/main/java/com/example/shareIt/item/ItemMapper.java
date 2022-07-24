package com.example.shareIt.item;

import lombok.Data;

@Data
public class ItemMapper {

    public static ItemDTO toItemDTO (Item item) {
        return new ItemDTO(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), item.getOwner(),
                item.getRequest());
    }
}
