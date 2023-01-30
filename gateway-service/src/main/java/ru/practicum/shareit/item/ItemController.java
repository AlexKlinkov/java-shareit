package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDTOInput;
import ru.practicum.shareit.item.dto.ItemDTOInput;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                             @RequestBody ItemDTOInput item) {
        log.info("Creating item={} by ownerId={}", item, ownerId);
        return itemClient.createItem(ownerId, item);
    }

    @PatchMapping(path = "/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                             @PathVariable Long itemId,
                                             @RequestBody ItemDTOInput item) {
        log.info("Updating item={} by ownerId={}", item, ownerId);
        return itemClient.updateItem(ownerId, itemId, item);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsOfUser(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Getting all items by ownerId={}", ownerId);
        return itemClient.getItemsOfUser(ownerId);
    }

    @GetMapping(path = "/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                              @PathVariable Long itemId) {
        log.info("Getting itemId ={} by ownerId={}", itemId, userId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemBySearchText(@RequestParam(value = "text", required = false) String text) {
        log.info("Getting items which contains text={}", text);
        return itemClient.getItemBySearchText(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody CommentDTOInput commentDTOInput) {
        log.info("Creating comment={} to itemId={} by userId={}", commentDTOInput, itemId, userId);
        return itemClient.addComment(userId, itemId, commentDTOInput);
    }
}
