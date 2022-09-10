package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findBookingsByItemIdAndItemOwnerIdAndStartAfter(Integer item_id, Integer item_owner_id,
                                                                  LocalDateTime localDateTime);

    List<Booking> findBookingsByItemIdAndItemOwnerIdAndEndBefore(Integer item_id, Integer item_owner_id,
                                                                 LocalDateTime localDateTime);

    Booking findBookingByBooker_IdAndItemIdAndEndBeforeAndStatusIs(Integer booker_id, Integer item_id,
                                                                   LocalDateTime localDateTime, TypeOfStatus status);

    Page<Booking> findAllByBookerId(Integer userId, Pageable pageable);

    Page<Booking> findAllByItemOwnerId(Integer userId, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartIsAfter(Integer userId, LocalDateTime localDateTime,
                                                   Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(Integer userId, LocalDateTime start,
                                                                 LocalDateTime finish, Pageable pageable);

    Page<Booking> findAllByBookerIdAndEndIsBefore(Integer userId, LocalDateTime localDateTime,
                                                  Pageable pageable);

    Page<Booking> findAllByBookerIdAndStatus(Integer userId, TypeOfStatus typeOfStatus,
                                             Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStartIsAfter(Integer userId, LocalDateTime localDateTime,
                                                      Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(Integer userId, LocalDateTime start,
                                                                    LocalDateTime finish, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndEndIsBefore(Integer userId, LocalDateTime localDateTime,
                                                     Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStatus(Integer userId, TypeOfStatus typeOfStatus,
                                                Pageable pageable);

}