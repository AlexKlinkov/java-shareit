package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDTOOutput {
    private Integer id;
    private String description;
    private User requestor;
    private List<ShortItemForAnswerOnQuery> items;
    private LocalDateTime created;
}