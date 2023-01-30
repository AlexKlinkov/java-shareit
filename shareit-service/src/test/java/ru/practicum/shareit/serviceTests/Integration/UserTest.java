package ru.practicum.shareit.serviceTests.Integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.errorHandlerException.NotFoundException;
import ru.practicum.shareit.user.*;

@SpringBootTest
@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
public class UserTest {
    @Autowired
    private ServiceUserInBD serviceUserInBD;
    @Autowired
    private UserRepository userRepository;
    private UserDTO userDTO;

    @BeforeEach
    public void init() {
        userDTO = new UserDTO(1, "Success", "Success@mail.ru");
    }

    @Test
    public void createUserTest() {
        Assertions.assertEquals(4, serviceUserInBD.create(userDTO).getId());
    }

    @Test
    public void updateUserTest() {
        UserDTO userDTOUpdate = new UserDTO(2, "Update", "Update@Update.ru");
        UserDTO userDTOReturn = serviceUserInBD.create(userDTO);
        userDTOUpdate.setId(3);
        Assertions.assertEquals(userDTOUpdate, serviceUserInBD.update(userDTOReturn.getId(), userDTOUpdate));
    }

    @Test
    public void getUsersTest() {
        serviceUserInBD.create(userDTO);
        Assertions.assertEquals(1, serviceUserInBD.getUsers().size());
    }

    @Test
    public void getUserByIdTestWhenUserIsNotExist() {
        Assertions.assertThrows(NotFoundException.class,
                () -> serviceUserInBD.getUserById(0));
    }

    @Test
    public void deleteByIdTest() {
        User user = userRepository.save(new User(1, "Name", "Sasha@mail.ru"));
        serviceUserInBD.deleteById(user.getId());
        Assertions.assertEquals(0, serviceUserInBD.getUsers().size());
    }
}