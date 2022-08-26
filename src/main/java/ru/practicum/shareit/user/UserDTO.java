package ru.practicum.shareit.user;

import com.fasterxml.jackson.annotation.JsonCreator;
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
    private Integer id;
    @NotNull
    @Pattern(regexp = "^\\S*$")
    private String name;
    @Email
    @NotNull
    private String email;
    //private List<ItemDTO> listWithAllItemsWhichBelongsOwner;

    public UserDTO() {
    }
}
