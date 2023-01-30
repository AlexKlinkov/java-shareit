package ru.practicum.shareit.booking;

import javax.annotation.processing.Generated;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDTO;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDTO;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-01-30T00:02:02+0300",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 11.0.15 (Amazon.com Inc.)"
)
public class BookingMapperImpl implements BookingMapper {

    @Override
    public Booking bookingFromBookingDTOInput(BookingDTOInput booking, Integer bookerId) {
        if ( booking == null && bookerId == null ) {
            return null;
        }

        Booking booking1 = new Booking();

        if ( booking != null ) {
            booking1.setStart( booking.getStart() );
            booking1.setEnd( booking.getEnd() );
        }

        return booking1;
    }

    @Override
    public BookingDTOOutput bookingDTOOutputFromBooking(Booking booking) {
        if ( booking == null ) {
            return null;
        }

        BookingDTOOutput bookingDTOOutput = new BookingDTOOutput();

        bookingDTOOutput.setId( booking.getId() );
        bookingDTOOutput.setStart( booking.getStart() );
        bookingDTOOutput.setEnd( booking.getEnd() );
        bookingDTOOutput.setBooker( userToUserDTO( booking.getBooker() ) );
        bookingDTOOutput.setItem( itemToItemDTO( booking.getItem() ) );
        bookingDTOOutput.setStatus( booking.getStatus() );

        return bookingDTOOutput;
    }

    protected UserDTO userToUserDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setId( user.getId() );
        userDTO.setName( user.getName() );
        userDTO.setEmail( user.getEmail() );

        return userDTO;
    }

    protected ItemDTO itemToItemDTO(Item item) {
        if ( item == null ) {
            return null;
        }

        ItemDTO itemDTO = new ItemDTO();

        if ( item.getId() != null ) {
            itemDTO.setId( item.getId() );
        }
        itemDTO.setName( item.getName() );
        itemDTO.setDescription( item.getDescription() );
        itemDTO.setAvailable( item.getAvailable() );
        itemDTO.setOwner( userToUserDTO( item.getOwner() ) );

        return itemDTO;
    }
}
