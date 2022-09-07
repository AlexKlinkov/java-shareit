package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTOInput {
    @NotNull
    @PositiveOrZero
    private Integer id;
    @NotNull
    @Pattern(regexp = "^\\S*$")
    private String name;
    @Email
    @NotNull
    private String email;
}
