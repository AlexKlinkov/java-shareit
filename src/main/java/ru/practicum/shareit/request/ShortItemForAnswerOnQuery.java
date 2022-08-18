package ru.practicum.shareit.request;

import lombok.Data;

@Data
public class ShortItemForAnswerOnQuery {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer ownerId;
    private Integer requestId;
}
