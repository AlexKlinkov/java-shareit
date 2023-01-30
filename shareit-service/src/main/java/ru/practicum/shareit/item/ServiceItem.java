
package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.CommentDTOInput;
import ru.practicum.shareit.item.comment.CommentDTOOutput;

import java.util.List;

public interface ServiceItem {
    ItemDTO create(int ownerId, ItemDTO item);

    ItemDTO update(int ownerId, int itemId, ItemDTO item);

    List<ItemDTO> getItems(int ownerId);

    ItemDTO getItemById(int userId, int itemId);

    List<ItemDTO> getItemBySearchText(String text);

    CommentDTOOutput addComment(int userId, int itemId, CommentDTOInput commentDTOInput);

    ItemDTO getItemDTOByRequestId(Integer requestId);
}
