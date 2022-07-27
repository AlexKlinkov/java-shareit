package ru.practicum.shareit.item;

import ru.practicum.shareit.request.ItemRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.UserDTO;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ItemDTO {
    transient int id;
    @NotEmpty
    String name;
    @NotEmpty
    String description;
    @NotNull
    Boolean available;
    UserDTO owner;
    ItemRequest request;
}
