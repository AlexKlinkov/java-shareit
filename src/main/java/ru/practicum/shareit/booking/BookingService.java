package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    BookingDTOOutput create(int user_id, BookingDTOInput booking);

    BookingDTOOutput updateStatusOfBooking (Integer ownerId, Integer bookingId, Boolean approved);
    BookingDTOOutput findBookingByIdAndUserId (int userId, int bookingId);
    List<BookingDTOOutput> getBookingsByBookerIdOrOwnerId (int userId, String state, String key);
}
