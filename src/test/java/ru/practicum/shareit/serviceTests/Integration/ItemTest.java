package ru.practicum.shareit.serviceTests.Integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.errorHandlerException.NotFoundException;
import ru.practicum.shareit.errorHandlerException.ValidationException;
import ru.practicum.shareit.item.ItemDTO;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ServiceItemInDB;
import ru.practicum.shareit.item.comment.CommentDTOInput;
import ru.practicum.shareit.item.comment.CommentDTOOutput;
import ru.practicum.shareit.user.ServiceUserInBD;
import ru.practicum.shareit.user.UserDTO;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
@DirtiesContext
public class ItemTest {

    @Autowired
    private ServiceItemInDB serviceItemInDB;
    @Autowired
    private ServiceUserInBD serviceUserInBD;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private ItemDTO itemDTO;
    private UserDTO owner;
    private UserDTO requestor;

    @BeforeEach
    public void init () {
        owner = new UserDTO(1, "OWNER", "Vase@mail.ru");
        requestor = new UserDTO(2, "REQUESTOR", "Requestor@mail.ru");
        itemDTO = new ItemDTO(1, "screwdriver", "screwdriverDescription", true,
                owner, null, null, new ArrayList<>(), null);
    }
    @Test
    public void createItemTest () {
        serviceUserInBD.create(owner);
        serviceUserInBD.create(requestor);
        ItemDTO itemDTOReturn = serviceItemInDB.create(15, itemDTO);
        itemDTO.setId(8);
        Assertions.assertEquals(itemDTO, itemDTOReturn);
    }

    @Test
    public void updateItemTest () {
        serviceUserInBD.create(owner);
        serviceUserInBD.create(requestor);
        ItemDTO itemDTOUpdate = new ItemDTO();
        itemDTOUpdate.setId(7);
        itemDTOUpdate.setDescription(null);
        itemDTOUpdate.setName(null);
        itemDTOUpdate.setOwner(null);
        itemDTOUpdate.setAvailable(null);
        serviceItemInDB.create(13, itemDTO);
        ItemDTO itemDTOReturn = serviceItemInDB.update(13, 7, itemDTOUpdate);
        itemDTO.setId(7);
        Assertions.assertEquals(itemDTO, itemDTOReturn);
    }

    @Test
    public void getItemsTest () {
        serviceUserInBD.create(owner);
        serviceUserInBD.create(requestor);
        serviceItemInDB.create(1, itemDTO);
        List<ItemDTO> itemDTOReturn = serviceItemInDB.getItems(1);
        Assertions.assertEquals(itemDTO, itemDTOReturn.get(0));
    }

    @Test
    public void getItemsByIDTestWhenError () {
        serviceUserInBD.create(owner);
        serviceUserInBD.create(requestor);
        serviceItemInDB.create(5, itemDTO);
        Assertions.assertThrows(
                NotFoundException.class,
                () -> serviceItemInDB.getItemById(1, 15)
        );
    }
    @Test
    public void getItemsBySearchTextTest () {
        serviceUserInBD.create(owner);
        serviceUserInBD.create(requestor);
        serviceItemInDB.create(7, itemDTO);
        List<ItemDTO> itemDTOReturn = serviceItemInDB.getItemBySearchText("screwdriver");
        Assertions.assertEquals(itemDTO.getName(), itemDTOReturn.get(0).getName());
    }
    @Test
    public void checkAlreadyExistItemTest () {
        serviceUserInBD.create(owner);
        serviceUserInBD.create(requestor);
        serviceItemInDB.create(17, itemDTO);
        boolean itemDTOReturn = serviceItemInDB.checkAlreadyExistItem(itemDTO);
        Assertions.assertTrue(itemDTOReturn);
    }

    @Test
    public void addCommentTestErrorWhenBookerDidNotTakeAnItem () {
        serviceUserInBD.create(owner);
        serviceUserInBD.create(requestor);
        serviceItemInDB.create(9, itemDTO);
        CommentDTOInput commentDTOInput = new CommentDTOInput();
        commentDTOInput.setText("comment");
        Assertions.assertThrows(ValidationException.class,
                () -> serviceItemInDB.addComment(2, 1, commentDTOInput)
        );
    }

    @Test
    public void addCommentTestErrorWhenCommentIsEmpty () {
        serviceUserInBD.create(owner);
        serviceUserInBD.create(requestor);
        serviceItemInDB.create(3, itemDTO);
        CommentDTOInput commentDTOInput = new CommentDTOInput();
        commentDTOInput.setText("");
        Assertions.assertThrows(ValidationException.class,
                () -> serviceItemInDB.addComment(1, 1, commentDTOInput)
        );
    }
    @Test
    public void addCommentTestWhenIsOk () throws InterruptedException {
        serviceUserInBD.create(owner);
        serviceUserInBD.create(requestor);
        serviceItemInDB.create(11, itemDTO);
        CommentDTOInput commentDTOInput = new CommentDTOInput();
        commentDTOInput.setText("goodComment");
        Booking booking = new Booking();
        booking.setItem(itemRepository.getById(6));
        booking.setBooker(userRepository.getById(12));
        booking.setStatus(TypeOfStatus.APPROVED);
        booking.setId(1);
        booking.setStart(LocalDateTime.now().plusSeconds(5));
        booking.setEnd(LocalDateTime.now().plusSeconds(7));
        bookingRepository.save(booking);
        Thread.sleep(8000);
        CommentDTOOutput commentDTOOutput = serviceItemInDB.addComment(12,6, commentDTOInput);
        Assertions.assertEquals(commentDTOInput.getText(), commentDTOOutput.getText());
    }

/*    @Test
    public void getItemDTOByRequestIdTest () {
        serviceUserInBD.create(owner);
        serviceUserInBD.create(requestor);
        serviceItemInDB.create(1, itemDTO);
        ItemRequestDTOInput itemRequestDTOInput = new ItemRequestDTOInput("hammer");
        itemRequestDTOInput.setId(1);
        serviceItemRequestInBD.create(2,  itemRequestDTOInput);
        ItemDTO itemDTOReturn = serviceItemInDB.getItemDTOByRequestId(1);
        Assertions.assertEquals(itemDTO.getRequestId(), itemDTOReturn.getRequestId());
    }*/
}
