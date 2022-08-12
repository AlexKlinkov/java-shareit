package ru.practicum.shareit.user;

import ru.practicum.shareit.item.ItemDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
@AllArgsConstructor
public class UserDTO {
    transient int id;
    @NotNull
    @Pattern(regexp = "^\\S*$")
    String name;
    @Email
    @NotNull
    String email;
    private List<ItemDTO> listWithAllItemsWhichBelongsOwner;
}
