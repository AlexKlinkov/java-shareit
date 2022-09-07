package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDTOInput;
import ru.practicum.shareit.booking.dto.TypeOfStatus;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }
    // Создание бронирования
    public ResponseEntity<Object> bookItem(long userId, BookingDTOInput requestDto) {
        return post("", userId, requestDto);
    }

    // Обновление информации по бронированию
    public ResponseEntity<Object> updateBooking(Long ownerId, Long bookingId, Boolean approved) {
        return patch("/" + bookingId, ownerId, approved);
    }

    // Получение бронирования по ID
    public ResponseEntity<Object> getBooking(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    // Получение всех бронирований
    public ResponseEntity<Object> getBookings(Long userId, TypeOfStatus state,
                                              Integer from, Integer size, String key) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size,
                "key", key
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }
    // Получение всех бронирований для владельца вещи
    public ResponseEntity<Object> getBookingsOfOwner(long userId, TypeOfStatus state,
                                                     Integer from, Integer size, String key) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size,
                "key", key
        );
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }
}
