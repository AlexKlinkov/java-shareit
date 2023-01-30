package ru.practicum.shareit.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.ServiceUserInBD;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserDTO;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
public class UserControllerTest {

    @InjectMocks
    private UserController controller;
    private final ObjectMapper mapper = new ObjectMapper();
    @Mock
    private ServiceUserInBD userService;
    private MockMvc mvc;
    private UserDTO userDTO;

    @BeforeEach
    public void init() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        userDTO = new UserDTO(1, "Max", "max@created.ru");
    }

    @Test
    public void createUserTest() throws Exception {
        when(userService.create(any()))
                .thenReturn(userDTO);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDTO.getName())))
                .andExpect(jsonPath("$.email", is(userDTO.getEmail())));
    }

    @Test
    public void getUsersTest() throws Exception {
        when(userService.getUsers())
                .thenReturn(List.of(userDTO));

        mvc.perform(get("/users")
                        .content(mapper.writeValueAsString(userDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].name", is(userDTO.getName())))
                .andExpect(jsonPath("$.[0].email", is(userDTO.getEmail())));
    }

    @Test
    public void updateUserTest() throws Exception {
        UserDTO updateUserDTO = new UserDTO(1, "Update", "update@update.ru");
        when(userService.update(1, updateUserDTO))
                .thenReturn(updateUserDTO);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(updateUserDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(updateUserDTO.getName())))
                .andExpect(jsonPath("$.email", is(updateUserDTO.getEmail())));
    }

    @Test
    public void getUsersByIdTest() throws Exception {
        when(userService.getUserById(1))
                .thenReturn(userDTO);

        mvc.perform(get("/users/1")
                        .content(mapper.writeValueAsString(userDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDTO.getName())))
                .andExpect(jsonPath("$.email", is(userDTO.getEmail())));
    }

    @Test
    public void deleteUsersByIdTest() throws Exception {
        doNothing().when(userService).deleteById(1);

        mvc.perform(delete("/users/1")
                        .content(mapper.writeValueAsString(userDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
