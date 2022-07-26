package com.example.shareIt.item;

import com.example.shareIt.request.ItemRequest;
import com.example.shareIt.user.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class Item {
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
