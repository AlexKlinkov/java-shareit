package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;

@Mapper
public interface BookingMapper {
    Booking bookingFromBookingDTOInput(BookingDTOInput booking, Integer bookerId);

    BookingDTOOutput bookingDTOOutputFromBooking(Booking booking);
}
