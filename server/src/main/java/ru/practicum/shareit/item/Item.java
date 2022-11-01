package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;;

@Entity
@Table(name = "items")
@Data
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotEmpty
    @Column(name = "name", nullable = false)
    private String name;
    @NotEmpty
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "is_available", nullable = false)
    private Boolean available;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;

    public Item() {

    }
}
