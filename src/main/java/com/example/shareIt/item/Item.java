package com.example.shareIt.item;

import com.example.shareIt.request.ItemRequest;
import com.example.shareIt.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Item {
    transient int id;
    String name;
    String description;
    boolean available;
    User owner;
    ItemRequest request;
}
