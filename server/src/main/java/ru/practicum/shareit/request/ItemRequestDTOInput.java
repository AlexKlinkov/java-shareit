package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDTOInput {
    @PositiveOrZero
    private int id;
    @NotNull
    @NotEmpty
    private String description;
}