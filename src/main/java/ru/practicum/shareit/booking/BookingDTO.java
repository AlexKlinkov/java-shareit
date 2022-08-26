package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDTO {
    private Integer id;
    private Integer bookerId;
    private LocalDateTime start;
    private LocalDateTime end;

    public BookingDTO() {
    }
}
