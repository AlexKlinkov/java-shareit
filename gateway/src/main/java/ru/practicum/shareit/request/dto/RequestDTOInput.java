package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserDTOInput;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestDTOInput {
    private Integer id;
    @NotNull
    @NotEmpty
    private String description;
    private UserDTOInput requestor;
    private LocalDateTime created;
}
