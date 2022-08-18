package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestDTOOutput {
    private Integer id;
    private String description;
    private User requestor;
    private List<ShortItemForAnswerOnQuery> items;
    private LocalDateTime created;
}
