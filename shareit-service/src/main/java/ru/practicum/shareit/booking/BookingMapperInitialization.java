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
    public Booking bookingFromBookingDTOInput(BookingDTOInput bookingDTO, Integer bookerId) {
        if (bookingDTO == null && bookerId == null) {
            return null;
        }

        Booking booking1 = new Booking();

        if (bookingDTO != null) {
            booking1.setStart(bookingDTO.getStart());
            booking1.setEnd(bookingDTO.getEnd());
            booking1.setItem(itemRepository.getById(bookingDTO.getItemId()));
            booking1.setBooker(userRepository.getById(bookerId));
            booking1.setStatus(TypeOfStatus.WAITING);
        }

        return booking1;
    }

    @Override
    public BookingDTOOutput bookingDTOOutputFromBooking(Booking booking) {
        if (booking == null) {
            return null;
        }

        Integer id;
        LocalDateTime start;
        LocalDateTime end;
        UserDTO booker;
        ItemDTO item;
        TypeOfStatus status;

        id = booking.getId();
        start = booking.getStart();
        end = booking.getEnd();
        booker = new UserDTO(booking.getBooker().getId(), booking.getBooker().getName(), booking.getBooker().getEmail());
        item = new ItemDTO(booking.getItem().getId(), booking.getItem().getName(), null, null, null,
                null, null, null, null);
        status = booking.getStatus();

        BookingDTOOutput bookingDTOOutput = new BookingDTOOutput(id, start, end, booker, item, status);

        return bookingDTOOutput;
    }
}