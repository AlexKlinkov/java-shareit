package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import ru.practicum.shareit.booking.BookingDTO;
import ru.practicum.shareit.item.comment.CommentDTOOutput;
import ru.practicum.shareit.request.ItemRequest;
import lombok.Data;
import ru.practicum.shareit.user.UserDTO;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemDTO {
    transient int id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    @NotNull
    Boolean available;
    private UserDTO owner;
    private BookingDTO lastBooking;
    private BookingDTO nextBooking;
    private List<CommentDTOOutput> comments;
    private Integer requestId;
}
