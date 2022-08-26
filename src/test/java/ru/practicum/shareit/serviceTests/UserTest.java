package ru.practicum.shareit.serviceTests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class UserTest {
    @Autowired
    private ServiceUserInBD serviceUserInBD;

    @BeforeEach
    public void init() {
        UserDTO userDTO = new UserDTO(1, "Success", "Success@mail.ru");
        serviceUserInBD.create(userDTO);
    }

    @AfterEach
    public void cleanAfterTest () {
        serviceUserInBD.deleteById(1);
    }

    @Test
    public void createUserTest() {
        Assertions.assertEquals(1, serviceUserInBD.getUserById(1).getId());
    }

    @Test
    public void getUsersTest() {
        Assertions.assertEquals(1, serviceUserInBD.getUsers().size());
    }

    @Test
    public void updateUserTest() {
        UserDTO userDTOUpdate = new UserDTO(1, "Update", "Update@Update.ru");
        Assertions.assertEquals(userDTOUpdate, serviceUserInBD.update(1, userDTOUpdate));
    }
}
