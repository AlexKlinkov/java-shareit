package ru.practicum.shareit.serviceTests.Integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.event.annotation.AfterTestExecution;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.*;

import javax.persistence.EntityManager;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
/*@Rollback(false)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = { "jdbc:h2:mem:shareit"})*/
@SpringBootTest
@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
public class UserTest {
    @Autowired
    private ServiceUserInBD serviceUserInBD;
    private UserDTO userDTO;

    @BeforeEach
    public void init() {
        userDTO = new UserDTO(1, "Success", "Success@mail.ru");
    }

    @Test
    public void createUserTest() {
        Assertions.assertEquals(3, serviceUserInBD.create(userDTO).getId());
    }

    @Test
    public void updateUserTest() {
        UserDTO userDTOUpdate = new UserDTO(2, "Update", "Update@Update.ru");
        serviceUserInBD.create(userDTO);
        Assertions.assertEquals(userDTOUpdate, serviceUserInBD.update(2, userDTOUpdate));
    }

    @Test
    public void getUsersTest() {
        serviceUserInBD.create(userDTO);
        Assertions.assertEquals(1, serviceUserInBD.getUsers().size());
    }
}
