
package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    BookingDTOOutput create(int user_id, BookingDTOInput booking);

    BookingDTOOutput updateStatusOfBooking(Integer ownerId, Integer bookingId, Boolean approved);

    BookingDTOOutput findBookingByIdAndUserId(int userId, int bookingId);

    List<BookingDTOOutput> getBookingsByOwnerIdOrBookingID(int userId, String state,
                                                           int from, int size, String key);
}