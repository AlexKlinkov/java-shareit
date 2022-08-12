package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDateTime;
import java.util.List;

@RepositoryRestResource(path = "bookings")
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findBookingsByItemIdAndItemOwnerIdAndStartAfter(Integer item_id, Integer item_owner_id,
                                                                  LocalDateTime localDateTime);

    List<Booking> findBookingsByItemIdAndItemOwnerIdAndEndBefore(Integer item_id, Integer item_owner_id,
                                                                 LocalDateTime localDateTime);

    Booking findBookingByBooker_IdAndItemIdAndEndBeforeAndStatusIs(Integer booker_id, Integer item_id,
                                                       LocalDateTime localDateTime, TypeOfStatus status);
}
