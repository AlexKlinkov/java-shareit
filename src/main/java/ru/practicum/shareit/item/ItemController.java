package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDTOInput;
import ru.practicum.shareit.item.comment.CommentDTOOutput;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ServiceItem serviceItem;

    @Autowired
    public ItemController(@Qualifier("ServiceItemInDB") ServiceItem ServiceItem) {
        this.serviceItem = ServiceItem;
    }

    @PostMapping
    public ItemDTO create(@RequestHeader("X-Sharer-User-Id") int ownerId, @Valid @RequestBody ItemDTO item) {
        return serviceItem.create(ownerId, item);
    }

    @PatchMapping(path = "/{itemId}")
    public ItemDTO update(@RequestHeader("X-Sharer-User-Id") int ownerId, @PathVariable int itemId,
                          @RequestBody ItemDTO item) {
        System.out.println("Я тут 1");
        return serviceItem.update(ownerId, itemId, item);
    }

    @GetMapping
    public List<ItemDTO> getItems(@RequestHeader("X-Sharer-User-Id") int ownerId) {
        System.out.println("Я тут 2");
        return serviceItem.getItems(ownerId);
    }

    @GetMapping(path = "/{itemId}")
    public ItemDTO getItemById(@RequestHeader(value = "X-Sharer-User-Id") int userId,
                               @PathVariable int itemId) {
        System.out.println("Я тут 3");
        return serviceItem.getItemById(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDTO> getItemBySearchText(@RequestParam(value = "text", required = false) String text) {
        return serviceItem.getItemBySearchText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDTOOutput addComment(@RequestHeader(value = "X-Sharer-User-Id") int userId,
                                       @PathVariable int itemId, @RequestBody CommentDTOInput commentDTOInput) {
        System.out.println("Я тут");
        return serviceItem.addComment(userId, itemId, commentDTOInput);
    }
}
