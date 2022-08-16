package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDTOInput {
    @NotNull
    @PositiveOrZero
    private Integer itemId;
    @FutureOrPresent
    private LocalDateTime start;
    @FutureOrPresent
    private LocalDateTime end;
}
