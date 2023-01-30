package ru.practicum.shareit.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.item.ItemDTO;
import ru.practicum.shareit.item.comment.CommentDTOOutput;
import ru.practicum.shareit.user.UserDTO;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
public class BookingControllerTest {

    @InjectMocks
    private BookingController controller;
    private final ObjectMapper mapper = new ObjectMapper();
    @Mock
    private ServiceBookingInBD bookingService;
    private MockMvc mvc;
    private BookingDTOInput bookingDTOInput;
    private BookingDTOOutput bookingDTOOutput;
    private UserDTO owner;
    private ItemDTO itemDTO;
    private BookingDTO lastBooking;
    private BookingDTO nextBooking;
    private CommentDTOOutput commentDTOOutput;
    private TypeOfStatus typeOfStatus;

    @BeforeEach
    public void init() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        owner = new UserDTO(1, "Name", "Email@email.ru");
        commentDTOOutput = new CommentDTOOutput(1, "text", "Peta", LocalDateTime.now());
        lastBooking = new BookingDTO(1, 2,
                LocalDateTime.of(2020, 2, 13, 2, 5),
                LocalDateTime.of(2020, 3, 13, 2, 5));
        nextBooking = new BookingDTO(2, 1,
                LocalDateTime.of(2021, 2, 13, 2, 5),
                LocalDateTime.of(2021, 3, 13, 2, 5));
        itemDTO = new ItemDTO(1, "screwdriver", "screwdriverDescription", true,
                owner, lastBooking, nextBooking, List.of(commentDTOOutput), 2);
        bookingDTOInput = new BookingDTOInput(6, LocalDateTime.now().plusSeconds(20),
                LocalDateTime.now().plusSeconds(50));
        typeOfStatus = TypeOfStatus.WAITING;
        bookingDTOOutput = new BookingDTOOutput(1, LocalDateTime.now().plusSeconds(20),
                LocalDateTime.now().plusSeconds(50),
                new UserDTO(2, "Booker", "Booker@mail.ru"), itemDTO, typeOfStatus);
    }

    @Test
    public void createBookingTest() throws Exception {
        when(bookingService.create(Mockito.anyInt(), Mockito.any(BookingDTOInput.class)))
                .thenReturn(bookingDTOOutput);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDTOOutput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDTOOutput.getId())));
    }

    @Test
    public void findBookingByIdAndUserIdTest() throws Exception {
        when(bookingService.findBookingByIdAndUserId(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(bookingDTOOutput);

        mvc.perform(get("/bookings/1")
                        .content(mapper.writeValueAsString(bookingDTOOutput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDTOOutput.getId())));
    }

    @Test
    public void getBookingsByBookerIdOrOwnerIdTest() throws Exception {
        when(bookingService.getBookingsByOwnerIdOrBookingID(Mockito.anyInt(), Mockito.anyString(),
                Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString()))
                .thenReturn(List.of(bookingDTOOutput));

        mvc.perform(get("/bookings")
                        .content(mapper.writeValueAsString(bookingDTOOutput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("state", "ALL")
                        .queryParam("from", "0")
                        .queryParam("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingDTOOutput.getId())));
    }

    @Test
    public void getBookingsByOwnerIdTest() throws Exception {
        when(bookingService.getBookingsByOwnerIdOrBookingID(Mockito.anyInt(), Mockito.anyString(),
                Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString()))
                .thenReturn(List.of(bookingDTOOutput));

        mvc.perform(get("/bookings/owner")
                        .content(mapper.writeValueAsString(bookingDTOOutput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("state", "ALL")
                        .queryParam("from", "0")
                        .queryParam("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingDTOOutput.getId())));
    }

    @Test
    public void updateStatusOfBookingTest() throws Exception {
        when(bookingService.updateStatusOfBooking(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyBoolean()))
                .thenReturn(bookingDTOOutput);
        bookingDTOOutput.setStatus(TypeOfStatus.APPROVED);

        mvc.perform(get("/bookings/1")
                        .content(mapper.writeValueAsString(bookingDTOOutput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2)
                        .queryParam("approved", "true"))
                .andExpect(status().isOk());

    }
}