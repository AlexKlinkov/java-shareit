package ru.practicum.shareit.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class ItemRequestDTOInput {
    @NotNull
    @NotEmpty
    private String description;
}
