
package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.ItemDTO;
import ru.practicum.shareit.user.UserDTO;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTOOutput {
    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private UserDTO booker;
    private ItemDTO item;
    private TypeOfStatus status;
}
