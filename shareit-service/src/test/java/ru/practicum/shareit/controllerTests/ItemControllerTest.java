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
import ru.practicum.shareit.booking.BookingDTO;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemDTO;
import ru.practicum.shareit.item.ServiceItemInDB;
import ru.practicum.shareit.item.comment.CommentDTOInput;
import ru.practicum.shareit.item.comment.CommentDTOOutput;
import ru.practicum.shareit.user.UserDTO;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
public class ItemControllerTest {

    @InjectMocks
    private ItemController controller;
    private final ObjectMapper mapper = new ObjectMapper();
    @Mock
    private ServiceItemInDB serviceItem;
    private MockMvc mvc;
    private ItemDTO itemDTO;
    private UserDTO owner;
    private BookingDTO lastBooking;
    private BookingDTO nextBooking;
    private CommentDTOOutput commentDTOOutput;

    @BeforeEach
    public void init() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        owner = new UserDTO(1, "Vase", "Vase@mail.ru");
        commentDTOOutput = new CommentDTOOutput(1, "text", "Peta", LocalDateTime.now());
        lastBooking = new BookingDTO(1, 2,
                LocalDateTime.of(2020, 2, 13, 2, 5),
                LocalDateTime.of(2020, 3, 13, 2, 5));
        nextBooking = new BookingDTO(2, 1,
                LocalDateTime.of(2021, 2, 13, 2, 5),
                LocalDateTime.of(2021, 3, 13, 2, 5));
        itemDTO = new ItemDTO(1, "screwdriver", "screwdriverDescription", true,
                owner, lastBooking, nextBooking, List.of(commentDTOOutput), 2);
    }

    @Test
    public void createItemTest() throws Exception {
        when(serviceItem.create(Mockito.anyInt(), Mockito.any(ItemDTO.class)))
                .thenReturn(itemDTO);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDTO.getId())))
                .andExpect(jsonPath("$.name", is(itemDTO.getName())));
    }

    @Test
    public void updateItemTest() throws Exception {
        when(serviceItem.update(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(ItemDTO.class)))
                .thenReturn(itemDTO);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDTO.getId())))
                .andExpect(jsonPath("$.name", is(itemDTO.getName())));
    }

    @Test
    public void getItemsTest() throws Exception {
        when(serviceItem.getItems(Mockito.anyInt()))
                .thenReturn(List.of(itemDTO));

        mvc.perform(get("/items")
                        .content(mapper.writeValueAsString(itemDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemDTO.getId())))
                .andExpect(jsonPath("$.[0].name", is(itemDTO.getName())));
    }

    @Test
    public void getItemByIdTest() throws Exception {
        when(serviceItem.getItemById(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(itemDTO);

        mvc.perform(get("/items/1")
                        .content(mapper.writeValueAsString(itemDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDTO.getId())))
                .andExpect(jsonPath("$.name", is(itemDTO.getName())));
    }

    @Test
    public void getItemBySearchTextTest() throws Exception {
        when(serviceItem.getItemBySearchText(Mockito.anyString()))
                .thenReturn(List.of(itemDTO));

        mvc.perform(get("/items/search")
                        .content(mapper.writeValueAsString(itemDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam("text", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemDTO.getId())))
                .andExpect(jsonPath("$.[0].name", is(itemDTO.getName())));
    }

    @Test
    public void addCommentTest() throws Exception {
        when(serviceItem.addComment(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(CommentDTOInput.class)))
                .thenReturn(commentDTOOutput);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDTOOutput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDTOOutput.getId())))
                .andExpect(jsonPath("$.text", is(commentDTOOutput.getText())));
    }
}