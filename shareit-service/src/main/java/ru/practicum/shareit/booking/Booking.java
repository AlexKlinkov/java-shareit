package ru.practicum.shareit.booking;

import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @FutureOrPresent
    @DateTimeFormat(pattern = "DD:MM:YYYY")
    @Column(name = "start_date")
    private LocalDateTime start;
    @FutureOrPresent
    @DateTimeFormat(pattern = "DD:MM:YYYY")
    @Column(name = "end_date")
    private LocalDateTime end;
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
    @ManyToOne
    @JoinColumn(name = "booker_id")
    private User booker;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TypeOfStatus status;
}
