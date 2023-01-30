package ru.practicum.shareit.serviceTests.Integration;

import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.errorHandlerException.NotFoundException;
import ru.practicum.shareit.errorHandlerException.ValidationException;
import ru.practicum.shareit.item.ServiceItemInDB;
import ru.practicum.shareit.request.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@SpringBootTest
@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
@DirtiesContext
@Data
public class RequestTest {

    @Qualifier("ServiceItemInDB")
    @Autowired
    private ServiceItemInDB serviceItem;
    @Autowired
    private ServiceItemRequestInBD serviceItemRequestInBD;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestMapperInitialization itemRequestMapper =
            new ItemRequestMapperInitialization(getServiceItem());

    @Test
    public void createRequestTestWhenUserIsNotExist() {
        Assertions.assertThrows(NotFoundException.class,
                () -> serviceItemRequestInBD.create(0, new ItemRequestDTOInput()));
    }

    @Test
    public void createRequestTestWhenIsOk() {
        User user = userRepository.save(User.builder().id(0).name("Name").email("Name@mail.ru").build());
        ItemRequestDTOInput itemRequestDTOInput = new ItemRequestDTOInput(0, "Description");
        Assertions.assertNotNull(serviceItemRequestInBD.create(user.getId(), itemRequestDTOInput));
    }

    @Test
    public void getItemRequestsOfUserTestWhenUserIsNotExist() {
        Assertions.assertThrows(NotFoundException.class,
                () -> serviceItemRequestInBD.getItemRequestsOfUser(0, 0, 1, ""));
    }

    @Test
    public void getItemRequestsOfUserTestWhenStartAndFinishEquels() {
        User user = userRepository.save(User.builder().id(0).name("Name").email("Name@mail.ru").build());
        Assertions.assertThrows(ValidationException.class,
                () -> serviceItemRequestInBD.getItemRequestsOfUser(user.getId(), 1, 1, ""));
    }

    @Test
    public void getItemRequestsOfUserTestWhenStartNegativeOrAmountIsNegative() {
        User user = userRepository.save(User.builder().id(0).name("Name").email("Name@mail.ru").build());
        Assertions.assertThrows(ValidationException.class,
                () -> serviceItemRequestInBD.getItemRequestsOfUser(user.getId(), -1, 1, ""));
    }

    @Test
    public void getItemRequestsOfUserTestWhenIsOk() {
        User user = userRepository.save(User.builder().id(0).name("Name").email("Name@mail.ru").build());
        ItemRequestDTOInput itemRequestDTOInput = new ItemRequestDTOInput(0, "Description");
        serviceItemRequestInBD.create(user.getId(), itemRequestDTOInput);
        List<ItemRequestDTOOutput> ListReturn =
                serviceItemRequestInBD.getItemRequestsOfUser(user.getId(), 0, 1, "id");
        Assertions.assertEquals(0, ListReturn.size());
    }

    @Test
    public void getRequestByRequestIdAndUserIdTestWhenUserIsNotExist() {
        Assertions.assertThrows(NotFoundException.class,
                () -> serviceItemRequestInBD.getRequestByRequestIdAndUserId(0, 1));
    }

    @Test
    public void getRequestByRequestIdAndUserIdTestWhenRequestIsNotExist() {
        Assertions.assertThrows(NotFoundException.class,
                () -> serviceItemRequestInBD.getRequestByRequestIdAndUserId(3, 0));
    }

    @Test
    public void getRequestByRequestIdAndUserIdTestWhenIsOk() {
        User user = userRepository.save(User.builder().id(0).name("Name").email("Name@mail.ru").build());
        ItemRequestDTOInput itemRequestDTOInput = new ItemRequestDTOInput(0, "Description");
        serviceItemRequestInBD.create(user.getId(), itemRequestDTOInput);
        Assertions.assertNotNull(serviceItemRequestInBD.getRequestByRequestIdAndUserId(user.getId(), 4));
    }

    @Test
    public void getItemRequestsByUserIdTestWhenUserIsNotExist() {
        Assertions.assertThrows(NotFoundException.class,
                () -> serviceItemRequestInBD.getItemRequestsByUserId(0));
    }

    @Test
    public void getItemRequestsByUserIdTestWhenIsOk() {
        User user = userRepository.save(User.builder().id(0).name("Name").email("Name@mail.ru").build());
        ItemRequestDTOInput itemRequestDTOInput = new ItemRequestDTOInput(0, "Description");
        serviceItemRequestInBD.create(user.getId(), itemRequestDTOInput);
        Assertions.assertNotNull(serviceItemRequestInBD.getItemRequestsByUserId(user.getId()));
    }

    @Test
    public void getItemRequestsByUserIdTestWhenRequestIsNULL() {
        User user = userRepository.save(User.builder().id(0).name("Name").email("Name@mail.ru").build());
        User userWithRequest = userRepository.save(User.builder().id(0).name("Name2").email("Name2@mail.ru").build());
        serviceItemRequestInBD.create(userWithRequest.getId(), new ItemRequestDTOInput(1, "Description"));
        Assertions.assertNotNull(serviceItemRequestInBD.getRequestByRequestIdAndUserId(user.getId(), 2));
    }
}