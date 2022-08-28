package ru.practicum.shareit.serviceTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.errorHandlerException.NotFoundException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.comment.CommentMapperInitialization;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDTO;
import ru.practicum.shareit.user.UserMapperImpl;
import ru.practicum.shareit.user.UserRepository;

import static org.mockito.Mockito.*;
@SpringBootTest
@AutoConfigureTestDatabase
@ExtendWith(MockitoExtension.class)
public class ItemTestMock {
    @InjectMocks
    private ServiceItemInDB serviceItemInDB;
    @Mock
    private ItemRepository itemRepository;
/*    @InjectMocks
    private UserRepository userRepository;
    @InjectMocks
    private BookingRepository bookingRepository;
    @InjectMocks
    private CommentRepository commentRepository;
    @InjectMocks
    private ItemRequestRepository itemRequestRepository;*/
    @Mock
    private ItemMapperInitialization itemMapperInitialization;
    @Mock
    private UserMapperImpl userMapper;
    @Mock
    private UserRepository userRepository;

    @Test
    public void createItemTest () {
        ItemDTO itemDTO = new ItemDTO();
        Item item = new Item();
        User user = new User();
        UserDTO userDTO = new UserDTO();
        when(userRepository.getById(1)).thenReturn(user);
        when(userMapper.DTOUserFromUser(user)).thenReturn(userDTO);
        when(itemMapperInitialization.itemFromItemDTO(itemDTO)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapperInitialization.itemDTOFromItem(item)).thenReturn(itemDTO);
        ItemDTO itemDTOReturn = serviceItemInDB.create(1, itemDTO);
        Assertions.assertEquals(itemDTO, itemDTOReturn);
    }

/*    @Test
    public void createItemWhenErrorTest () {
        ItemDTO itemDTO = new ItemDTO();
        when(itemRepository.save(any(Item.class))).thenThrow(NotFoundException.class);
        Assertions.assertThrows(
                NotFoundException.class,
                () -> serviceItemInDB.update(1, 1,  itemDTO)
        );
    }

    @Test
    public void updateItemWhenErrorTest () {
        ItemDTO itemDTO = new ItemDTO();
        when(itemRepository.save(any(Item.class))).thenThrow(RuntimeException.class);
        Assertions.assertThrows(
                RuntimeException.class,
                () -> serviceItemInDB.update(1, 1, itemDTO)
        );
    }*/
}
