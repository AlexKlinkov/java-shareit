package com.example.shareIt.request;

import com.example.shareIt.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.FutureOrPresent;
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
