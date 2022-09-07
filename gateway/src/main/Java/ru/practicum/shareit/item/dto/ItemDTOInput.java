package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDTOInput;
import ru.practicum.shareit.user.dto.UserDTOInput;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDTOInput {
    @PositiveOrZero
    private Integer id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    @NotNull
    private Boolean available;
    private UserDTOInput owner;
    private BookingDTOInput lastBooking;
    private BookingDTOInput nextBooking;
    private List<CommentDTOInput> comments;
}
