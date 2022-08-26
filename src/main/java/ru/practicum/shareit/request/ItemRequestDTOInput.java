package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ItemRequestDTOInput {
    @NotNull
    @NotEmpty
    private String description;

    public ItemRequestDTOInput() {
    }
}
