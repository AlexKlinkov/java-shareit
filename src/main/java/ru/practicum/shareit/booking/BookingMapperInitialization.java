package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemDTO;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserDTO;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

@Component("BookingMapperInitialization")
public class BookingMapperInitialization implements BookingMapper {

        private final UserRepository userRepository;
        private final ItemRepository itemRepository;

    @Autowired
    public BookingMapperInitialization(UserRepository userRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
        public Booking bookingFromBookingDTOInput(BookingDTOInput booking, Integer bookerId) {
            if ( booking == null && bookerId == null ) {
                return null;
            }

            Booking booking1 = new Booking();

            if ( booking != null ) {
                booking1.setStart( booking.getStart() );
                booking1.setEnd( booking.getEnd() );
                booking1.setItem(itemRepository.getById(booking.getItemId()));
                booking1.setBooker( userRepository.getById(bookerId));
                booking1.setStatus(TypeOfStatus.WAITING);
            }

            return booking1;
        }

        @Override
        public BookingDTOOutput bookingDTOOutputFromBooking(Booking booking) {
            if ( booking == null ) {
                return null;
            }

            Integer id = null;
            LocalDateTime start = null;
            LocalDateTime end = null;
            UserDTO booker = null;
            ItemDTO item = null;
            TypeOfStatus status = null;

            id = booking.getId();
            start = booking.getStart();
            end = booking.getEnd();
            booker = new UserDTO(booking.getBooker().getId(), booking.getBooker().getName(), booking.getBooker().getEmail(), null);
            item = new ItemDTO(booking.getItem().getId(), booking.getItem().getName(), null, null, null,
                    null, null, null, null);
            status = booking.getStatus();

            BookingDTOOutput bookingDTOOutput = new BookingDTOOutput( id, start, end, booker,item, status );

            return bookingDTOOutput;
        }
    }
