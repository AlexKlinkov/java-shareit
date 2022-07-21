package com.example.shareIt.booking;

import com.example.shareIt.item.Item;
import com.example.shareIt.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Future;
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
