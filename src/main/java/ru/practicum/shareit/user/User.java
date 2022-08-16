package ru.practicum.shareit.user;

import ru.practicum.shareit.item.Item;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name", nullable = false)
    @Pattern(regexp = "^\\S*$")
    private String name;
    @Email
    @NotNull
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    @Transient
    private List<Item> listWithAllItemsWhichBelongsOwner;
}
