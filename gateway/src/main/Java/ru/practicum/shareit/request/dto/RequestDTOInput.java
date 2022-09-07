package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserDTOInput;

import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestDTOInput {
    @PositiveOrZero
    private Integer id;
    private String description;
    private UserDTOInput requestor;
    @PositiveOrZero
    private LocalDateTime created;
}
