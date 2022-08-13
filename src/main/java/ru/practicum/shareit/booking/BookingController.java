package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(@Qualifier("ServiceBookingInBD") BookingService bookingService) {
        this.bookingService = bookingService;
    }


    @PostMapping("/bookings")
    public BookingDTOOutput create(@RequestHeader("X-Sharer-User-Id") int user_id,
                           @Valid @RequestBody BookingDTOInput bookingDTO) {
        return bookingService.create(user_id, bookingDTO);

    }

    @PatchMapping(path = "/bookings/{bookingId}")
    public BookingDTOOutput updateStatusOfBooking (@RequestHeader("X-Sharer-User-Id") int ownerId,
                                    @PathVariable int bookingId,
                                    @RequestParam(value = "approved") Boolean approved) {
        return bookingService.updateStatusOfBooking(ownerId, bookingId, approved);
    }

    @GetMapping(path = "/bookings/{bookingId}")
    public BookingDTOOutput findBookingByIdAndUserId (@RequestHeader("X-Sharer-User-Id") int userId,
                                                              @PathVariable int bookingId) {
        return bookingService.findBookingByIdAndUserId(userId, bookingId);

    }
    @GetMapping("/bookings")
    public List<BookingDTOOutput> getBookingsByBookerIdOrOwnerId (@RequestHeader("X-Sharer-User-Id") int userId,
                                                       @RequestParam(value = "state", required = false,
                                                               defaultValue = "ALL") String state, String key) {
        return bookingService.getBookingsByBookerIdOrOwnerId(userId, state, "booker");
    }

    @GetMapping(path = "/bookings/owner")
    public List<BookingDTOOutput> getBookingsByOwnerId (@RequestHeader("X-Sharer-User-Id") int userId,
                                                       @RequestParam(value = "state", required = false,
                                                               defaultValue = "ALL") String state, String key) {
        return bookingService.getBookingsByBookerIdOrOwnerId(userId, state, "owner");
    }
}
