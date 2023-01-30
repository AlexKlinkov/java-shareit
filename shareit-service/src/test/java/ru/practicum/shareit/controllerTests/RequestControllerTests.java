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
import ru.practicum.shareit.request.*;
import ru.practicum.shareit.user.User;

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
public class RequestControllerTests {

    @InjectMocks
    private ItemRequestController controller;
    private final ObjectMapper mapper = new ObjectMapper();
    @Mock
    private ServiceItemRequestInBD serviceItemRequestInBD;
    private MockMvc mvc;
    private ItemRequestDTOInput itemRequestDTOInput;
    private ItemRequestDTOOutput itemRequestDTOOutput;
    private ShortItemForAnswerOnQuery item;
    List<ShortItemForAnswerOnQuery> items;
    private User requestor;
    private User ownerItem;

    @BeforeEach
    public void init() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        itemRequestDTOInput = new ItemRequestDTOInput(1, "description");
        ownerItem = new User(1, "Owner", "Owner@mail.ru");
        requestor = new User(2, "Oleg", "Oleg@mail.ru");
        item = new ShortItemForAnswerOnQuery(1, "Отвертка", "screwdriver",
                true, ownerItem.getId(), 2);
        items = List.of(item);
        itemRequestDTOOutput = new ItemRequestDTOOutput(1, "description", requestor, items,
                LocalDateTime.of(2022, 5, 6, 4, 5, 8));
    }

    @Test
    public void createRequestTest() throws Exception {
        when(serviceItemRequestInBD.create(2, itemRequestDTOInput))
                .thenReturn(itemRequestDTOOutput);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDTOOutput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDTOOutput.getId())))
                .andExpect(jsonPath("$.description", is(itemRequestDTOOutput.getDescription())));
    }

    @Test
    public void getItemRequestsByUserIdTest() throws Exception {
        when(serviceItemRequestInBD.getItemRequestsByUserId(Mockito.anyInt()))
                .thenReturn(List.of(itemRequestDTOOutput));

        mvc.perform(get("/requests")
                        .content(mapper.writeValueAsString(itemRequestDTOOutput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemRequestDTOOutput.getId())))
                .andExpect(jsonPath("$.[0].description", is(itemRequestDTOOutput.getDescription())));
    }

    @Test
    public void getRequestByRequestIdAndUserIdTest() throws Exception {
        when(serviceItemRequestInBD.getRequestByRequestIdAndUserId(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(itemRequestDTOOutput);

        mvc.perform(get("/requests/1")
                        .content(mapper.writeValueAsString(itemRequestDTOOutput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDTOOutput.getId())))
                .andExpect(jsonPath("$.description", is(itemRequestDTOOutput.getDescription())));
    }

    @Test
    public void getItemRequestsOfUserTest() throws Exception {
        when(serviceItemRequestInBD.getItemRequestsOfUser(Mockito.anyInt(), Mockito.anyInt(),
                Mockito.anyInt(), Mockito.anyString()))
                .thenReturn(List.of(itemRequestDTOOutput));

        mvc.perform(get("/requests/all")
                        .content(mapper.writeValueAsString(itemRequestDTOOutput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemRequestDTOOutput.getId())))
                .andExpect(jsonPath("$.[0].description", is(itemRequestDTOOutput.getDescription())));
    }
}