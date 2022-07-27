package ru.practicum.shareit.request;

import ru.practicum.shareit.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemRequest {
    @PositiveOrZero
    transient int id;
    String description;
    User requestor;
    LocalDateTime created;
}
