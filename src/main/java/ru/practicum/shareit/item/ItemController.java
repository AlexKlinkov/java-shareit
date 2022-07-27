package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ServiceItem ServiceItem;

    @Autowired
    public ItemController(@Qualifier("ServiceItemInMemory") ServiceItem ServiceItem) {
        this.ServiceItem = ServiceItem;
    }

    @PostMapping
    public ItemDTO create(@RequestHeader("X-Sharer-User-Id") int ownerId, @Valid @RequestBody ItemDTO item) {
       return ServiceItem.create(ownerId, item);
    }

    @PatchMapping(path = "/{itemId}")
    public ItemDTO update (@RequestHeader("X-Sharer-User-Id") int ownerId, @PathVariable int itemId,
                           @RequestBody ItemDTO item) {
        return ServiceItem.update(ownerId, itemId, item);
    }

    @GetMapping
    public List<ItemDTO> getItems (@RequestHeader("X-Sharer-User-Id") int ownerId) {
        return ServiceItem.getItems(ownerId);
    }

    @GetMapping(path = "/{itemId}")
    public ItemDTO getUserById (@PathVariable int itemId) {
        return ServiceItem.getItemById(itemId);
    }

    @GetMapping("/search")
    public List<ItemDTO> getItemBySearchText (@RequestParam(value = "text", required = false) String text) {
        return ServiceItem.getItemBySearchText(text);
    }
}
