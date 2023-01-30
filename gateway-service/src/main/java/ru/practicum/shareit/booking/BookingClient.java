package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDTOInput;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

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
    public ResponseEntity<Object> updateBooking(Long bookingId, Long ownerId, Boolean approved) {
        String path = "/" + bookingId + "?approved=" + approved;
        return patch(path, ownerId, null, null);
    }

    // Получение бронирования по ID
    public ResponseEntity<Object> getBooking(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    // Получение всех бронирований
    public ResponseEntity<Object> getBookings(Long userId, String state,
                                              Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    // Получение всех бронирований для владельца вещи
    public ResponseEntity<Object> getBookingsOfOwner(long userId, String state,
                                                     Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }
}
