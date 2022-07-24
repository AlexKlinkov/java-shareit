package com.example.shareIt.item;

import com.example.shareIt.errorHandlerException.ValidationException;
import com.example.shareIt.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final StorageItem storageItem;

    @Autowired
    public ItemController(@Qualifier("StorageItemInMemory") StorageItem storageItem) {
        this.storageItem = storageItem;
    }

    @PostMapping
    public ItemDTO create(@RequestHeader("X-Sharer-User-Id") int ownerId, @Valid @RequestBody ItemDTO item) {
       return storageItem.create(ownerId, item);
    }

    @PatchMapping(path = "/{itemId}")
    public ItemDTO update (@RequestHeader("X-Sharer-User-Id") int ownerId, @PathVariable int itemId,
                           @RequestBody ItemDTO item) {
        return storageItem.update(ownerId, itemId, item);
    }

    @GetMapping
    public List<ItemDTO> getItems (@RequestHeader("X-Sharer-User-Id") int ownerId) {
        return storageItem.getItems(ownerId);
    }

    @GetMapping(path = "/{itemId}")
    public ItemDTO getUserById (@PathVariable int itemId) {
        return storageItem.getItemById(itemId);
    }
}
