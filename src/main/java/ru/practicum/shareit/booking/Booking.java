package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Booking {
    transient int id;
    LocalDate start;
    LocalDate end;
    Item item;
    User booker;
    String status;
}
