package ru.practicum.shareit.serviceTests.Integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.errorHandlerException.MyMethodArgumentTypeMismatchException;
import ru.practicum.shareit.errorHandlerException.NotFoundException;
import ru.practicum.shareit.errorHandlerException.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDTO;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ServiceItemInDB;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapperImpl;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

@SpringBootTest
@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
@DirtiesContext
public class BookingTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ServiceBookingInBD serviceBookingInBD;
    @Autowired
    private ServiceItemInDB serviceItemInDB;
    @Autowired
    private UserMapperImpl userMapper;
    @Autowired
    private BookingMapperInitialization bookingMapperInitialization;

    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private BookingDTOInput booking;

    @BeforeEach
    public void init() {
        booking = new BookingDTOInput();
        booking.setStart(LocalDateTime.now().plusSeconds(15));
        booking.setEnd(LocalDateTime.now().plusSeconds(30));
        booking.setItemId(1);
    }

    @Test
    public void createBookingTestWhenUserIsEmpty() {
        Assertions.assertThrows(NotFoundException.class,
                () -> serviceBookingInBD.create(1, booking));
    }

    @Test
    public void createBookingTestWhenItemIsEmpty() {
        userRepository.save(User.builder().id(1).name("Petre").email("Petre@mail.ru").build());
        Assertions.assertThrows(NotFoundException.class,
                () -> serviceBookingInBD.create(1, booking));
    }

    @Test
    public void createBookingTestWhenOwnerTryBooking() {
        User owner = userRepository.save(User.builder().id(2).name("Petre").email("Petre@mail.ru").build());
        itemRepository.save(
                new Item(1,
                        "tool1", "tool1Description", true, owner, null));
        Assertions.assertThrows(NotFoundException.class,
                () -> serviceBookingInBD.create(2, booking));
    }

    @Test
    public void createBookingTestWhenItemIsNotAvailable() {
        User owner = userRepository.save(User.builder().id(10).name("Petre").email("Petre@mail.ru").build());
        User booker = userRepository.save(User.builder().id(9).name("Booker").email("Booker@mail.ru").build());
        Item item = itemRepository.save(
                new Item(6,
                        "tool1", "tool1Description", false, owner, null));
        booking.setItemId(item.getId());
        Assertions.assertThrows(ValidationException.class,
                () -> serviceBookingInBD.create(booker.getId(), booking));
    }

    @Test
    public void createBookingTestWhenIsOk() {
        User owner = userRepository.save(User.builder().id(5).name("Petre").email("Petre@mail.ru").build());
        User booker = userRepository.save(User.builder().id(6).name("Booker").email("Booker@mail.ru").build());
        Item item = itemRepository.save(
                new Item(3,
                        "tool1", "tool1Description", true, owner, null));
        booking.setItemId(item.getId());
        BookingDTOOutput bookingDTOOutput = serviceBookingInBD.create(booker.getId(), booking);
        Assertions.assertEquals(booking.getItemId(), bookingDTOOutput.getItem().getId());
    }

    @Test
    public void updateStatusBookingTestWhenStatusAlreadyApproved() {
        User owner = userRepository.save(User.builder().id(8).name("Petre").email("Petre@mail.ru").build());
        User booker = userRepository.save(User.builder().id(7).name("Booker").email("Booker@mail.ru").build());
        Item item = itemRepository.save(
                new Item(5,
                        "tool1", "tool1Description", true, owner, null));
        booking.setItemId(item.getId());
        BookingDTOOutput bookingReturn = serviceBookingInBD.create(booker.getId(), booking);
        bookingRepository.getById(bookingReturn.getId()).setStatus(TypeOfStatus.APPROVED);
        Assertions.assertThrows(ValidationException.class,
                () -> serviceBookingInBD.updateStatusOfBooking(owner.getId(), bookingReturn.getId(),
                        true));
    }

    @Test
    public void updateStatusBookingTestWhenTryUpdateNotOwner() {
        User owner = userRepository.save(User.builder().id(1).name("Petre").email("Petre@mail.ru").build());
        User booker = userRepository.save(User.builder().id(2).name("Booker").email("Booker@mail.ru").build());
        Item item = itemRepository.save(
                new Item(3,
                        "tool1", "tool1Description", true, owner, null));
        booking.setItemId(item.getId());
        BookingDTOOutput bookingReturn = serviceBookingInBD.create(booker.getId(), booking);
        Assertions.assertThrows(NotFoundException.class,
                () -> serviceBookingInBD.updateStatusOfBooking(0, bookingReturn.getId(), true));
    }

    @Test
    public void updateStatusBookingTestWhenIsOk() {
        User owner = userRepository.save(User.builder().id(7).name("Petre").email("Petre@mail.ru").build());
        User booker = userRepository.save(User.builder().id(8).name("Booker").email("Booker@mail.ru").build());
        Item item = itemRepository.save(
                new Item(4,
                        "tool1", "tool1Description", true, owner, null));
        booking.setItemId(item.getId());
        BookingDTOOutput whichWillUpdate = serviceBookingInBD.create(booker.getId(), booking);
        BookingDTOOutput bookingDTOOutput = serviceBookingInBD.updateStatusOfBooking(owner.getId(),
                whichWillUpdate.getId(), true);
        Assertions.assertEquals(TypeOfStatus.APPROVED, bookingDTOOutput.getStatus());
    }

    @Test
    public void updateStatusBookingTestWhenIsNotOk() {
        User owner = userRepository.save(User.builder().id(1).name("Petre").email("Petre@mail.ru").build());
        User booker = userRepository.save(User.builder().id(2).name("Booker").email("Booker@mail.ru").build());
        Item item = itemRepository.save(
                new Item(1,
                        "tool1", "tool1Description", true, owner, null));
        booking.setItemId(item.getId());
        serviceBookingInBD.create(booker.getId(), booking);
        BookingDTOOutput bookingDTOOutput = serviceBookingInBD.updateStatusOfBooking(owner.getId(), 1,
                false);
        Assertions.assertEquals(TypeOfStatus.REJECTED, bookingDTOOutput.getStatus());
    }

    @Test
    public void findBookingByIdAndUserIdTestWhenBookingIsNotExist() {
        Assertions.assertThrows(NotFoundException.class,
                () -> serviceBookingInBD.findBookingByIdAndUserId(1, 800));
    }

    @Test
    public void findBookingByIdAndUserIdTestWhenBookingIsNotBelongsOwnerOrBooker() {
        Assertions.assertThrows(NotFoundException.class,
                () -> serviceBookingInBD.findBookingByIdAndUserId(1, 8));
    }

    @Test
    public void findBookingByIdAndUserIdTestWhenIsOk() {
        User owner = userRepository.save(User.builder().id(3).name("thirdOwner").email("third@mail.ru").build());
        User booker = userRepository.save(User.builder().id(4).name("bookerFourth").email("Fourth@mail.ru").build());
        itemRepository.save(new Item(2, "secondItem", "Description", true,
                owner, null));
        booking.setItemId(2);
        serviceBookingInBD.create(booker.getId(), booking);
        Assertions.assertNotNull(serviceBookingInBD.findBookingByIdAndUserId(booker.getId(), 2));
    }

    @Test
    public void getBookingsByBookerIdOrOwnerIdWhenStatusIsNotCorrect() {
        Assertions.assertThrows(MyMethodArgumentTypeMismatchException.class,
                () -> serviceBookingInBD.getBookingsByOwnerIdOrBookingID(0, "StatusNotCorrect",
                        0, 1, "booker"));
    }

    @Test
    public void getBookingsByBookerIdOrOwnerIdWhenUserIsNotExist() {
        Assertions.assertThrows(NotFoundException.class,
                () -> serviceBookingInBD.getBookingsByOwnerIdOrBookingID(10000, "ALL",
                        0, 1, "booker"));
    }

    @Test
    public void getBookingsByBookerIdOrOwnerIdWhenALLIsOK() {
        User user = userRepository.save(User.builder().id(4).name("bookerFourth").email("Fourth@mail.ru").build());
        Assertions.assertEquals(0,
                serviceBookingInBD.getBookingsByOwnerIdOrBookingID(user.getId(), "ALL",
                        0, 1, "").size());
    }

    @Test
    public void getBookingsByBookerIdOrOwnerIdWhenFUTUREBooking() {
        User booker = userRepository.save(User.builder().id(1).name("Booker").email("Booker@mail.ru").build());
        Assertions.assertEquals(0,
                serviceBookingInBD.getBookingsByOwnerIdOrBookingID(booker.getId(), "FUTURE",
                        0, 1, "ALL").size());
    }

    @Test
    public void getBookingsByBookerIdOrOwnerIdWhenCURRENTBooking() {
        User user = userRepository.save(User.builder().id(1).name("Name").email("email@email").build());
        Assertions.assertEquals(0,
                serviceBookingInBD.getBookingsByOwnerIdOrBookingID(user.getId(), "CURRENT",
                        0, 1, "ALL").size());
    }

    @Test
    public void getBookingsByBookerIdOrOwnerIdWhenPASTBooking() {
        User user = userRepository.save(User.builder().id(4).name("bookerFourth").email("Fourth@mail.ru").build());
        Assertions.assertEquals(0,
                serviceBookingInBD.getBookingsByOwnerIdOrBookingID(user.getId(), "PAST",
                        0, 1, "ALL").size());
    }

    @Test
    public void getBookingsByBookerIdOrOwnerIdWhenFUTUREOwner() {
        User booker = userRepository.save(User.builder().id(1).name("Booker").email("Booker@mail.ru").build());
        Assertions.assertEquals(0,
                serviceBookingInBD.getBookingsByOwnerIdOrBookingID(booker.getId(), "FUTURE",
                        0, 1, "owner").size());
    }

    @Test
    public void getBookingsByBookerIdOrOwnerIdWhenCURRENTOwner() {
        User user = userRepository.save(User.builder().id(1).name("Name").email("email@email").build());
        Assertions.assertEquals(0,
                serviceBookingInBD.getBookingsByOwnerIdOrBookingID(user.getId(), "CURRENT",
                        0, 1, "owner").size());
    }

    @Test
    public void getBookingsByBookerIdOrOwnerIdWhenPASTOwner() {
        User user = userRepository.save(User.builder().id(4).name("bookerFourth").email("Fourth@mail.ru").build());
        Assertions.assertEquals(0,
                serviceBookingInBD.getBookingsByOwnerIdOrBookingID(user.getId(), "PAST",
                        0, 1, "owner").size());
    }

    @Test
    public void getBookingsByBookerIdOrOwnerIdWhenWATINGBooking() {
        User user = userRepository.save(User.builder().id(4).name("bookerFourth").email("Fourth@mail.ru").build());
        Assertions.assertEquals(0,
                serviceBookingInBD.getBookingsByOwnerIdOrBookingID(user.getId(), "WAITING",
                        0, 1, "ALL").size());
    }

    @Test
    public void getBookingsByBookerIdOrOwnerIdWhenREJECTBookingOwner() {
        User booker = userRepository.save(User.builder().id(1).name("Booker").email("Booker@mail.ru").build());
        Assertions.assertEquals(0,
                serviceBookingInBD.getBookingsByOwnerIdOrBookingID(booker.getId(), "REJECTED",
                        0, 1, "owner").size());
    }

    @Test
    public void getBookingsByBookerIdOrOwnerIdWhenWATINGOwner() {
        User user = userRepository.save(User.builder().id(4).name("bookerFourth").email("Fourth@mail.ru").build());
        Assertions.assertEquals(0,
                serviceBookingInBD.getBookingsByOwnerIdOrBookingID(user.getId(), "WAITING",
                        0, 1, "owner").size());
    }

    @Test
    public void getBookingsByBookerIdOrOwnerIdWhenREJECTBookingBOOKER() {
        User booker = userRepository.save(User.builder().id(1).name("Booker").email("Booker@mail.ru").build());
        Assertions.assertEquals(0,
                serviceBookingInBD.getBookingsByOwnerIdOrBookingID(booker.getId(), "REJECTED",
                        0, 1, "ALL").size());
    }

    @Test
    public void getBookingsByBookerIdOrOwnerIdWhenREJECTBookingOWNER() {
        User owner = userRepository.save(User.builder().id(1).name("Petre").email("Petre@mail.ru").build());
        User booker = userRepository.save(User.builder().id(1).name("Booker").email("Booker@mail.ru").build());
        Item item = itemRepository.save(new Item(
                1,
                "Name",
                "Description",
                true,
                owner,
                null
        ));
        BookingDTOInput bookingDTOInput = new BookingDTOInput(
                item.getId(),
                LocalDateTime.now().plusSeconds(15),
                LocalDateTime.now().plusSeconds(30));
        BookingDTOOutput bookingDTOOutput = serviceBookingInBD.create(booker.getId(), bookingDTOInput);
        bookingRepository.getById(bookingDTOOutput.getId()).setStatus(TypeOfStatus.REJECTED);
        System.out.println(userRepository.findAll());
        Assertions.assertEquals(1,
                serviceBookingInBD.getBookingsByOwnerIdOrBookingID(owner.getId(), "REJECTED",
                        0, 1, "owner").size());
    }

    @Test
    public void getBookingsByBookerIdOrOwnerIdTestWhenStartAndFinishEquels() {
        User user = userRepository.save(User.builder().id(0).name("Name").email("Name@mail.ru").build());
        Assertions.assertThrows(ValidationException.class,
                () -> serviceBookingInBD.getBookingsByOwnerIdOrBookingID(user.getId(), "ALL", 1, 0,
                        "ALL"));
    }

    @Test
    public void getBookingsByBookerIdOrOwnerIdTestWhenStartNegativeOrAmountIsNegative() {
        User user = userRepository.save(User.builder().id(0).name("Name").email("Name@mail.ru").build());
        Assertions.assertThrows(ValidationException.class,
                () -> serviceBookingInBD.getBookingsByOwnerIdOrBookingID(user.getId(), "ALL", -1, 1,
                        ""));
    }

    @Test
    public void getBookingDTOOutputsTest1() {
        int from = 0;
        int size = 1;
        int iterations = 0;
        Page<Booking> page = bookingRepository.findAllByItemOwnerId(1, PageRequest.of(0, size));
        serviceBookingInBD.getBookingDTOOutputs(page, from, size, iterations);
        Assertions.assertNotNull(serviceBookingInBD.getFinalReturnList());
    }

    @Test
    public void getBookingDTOOutputsTest2() {
        int from = 2;
        int size = 1;
        int iterations = 0;
        Page<Booking> page = bookingRepository.findAllByItemOwnerId(1, PageRequest.of(0, size));
        serviceBookingInBD.getBookingDTOOutputs(page, from, size, iterations);
        Assertions.assertNotNull(serviceBookingInBD.getFinalReturnList());
    }

    @Test
    public void getBookingDTOOutputsTest3() {
        int from = 2;
        int size = 2;
        int iterations = 0;
        Page<Booking> page = bookingRepository.findAllByItemOwnerId(1, PageRequest.of(0, size));
        serviceBookingInBD.getBookingDTOOutputs(page, from, size, iterations);
        Assertions.assertNotNull(serviceBookingInBD.getFinalReturnList());
    }

    @Test
    public void getBookingDTOOutputsTest4() {
        int from = 4;
        int size = 3;
        int iterations = 1;
        Page<Booking> page = bookingRepository.findAllByItemOwnerId(1, PageRequest.of(0, size));
        serviceBookingInBD.getBookingDTOOutputs(page, from, size, iterations);
        Assertions.assertNotNull(serviceBookingInBD.getFinalReturnList());
    }

    @Test
    public void getBookingDTOOutputsTest5() {
        int from = 4;
        int size = 3;
        int iterations = 2;
        Page<Booking> page = bookingRepository.findAllByItemOwnerId(1, PageRequest.of(0, size));
        serviceBookingInBD.getBookingDTOOutputs(page, from, size, iterations);
        Assertions.assertNotNull(serviceBookingInBD.getFinalReturnList());
    }

    @Test
    public void createTestWhenItemIsNotAvailable() {
        User owner = userRepository.save(User.builder().id(0).name("Name").email("myEmail@mail.ru").build());
        User firstBooker = userRepository.save(User.builder().id(0).name("Name2").email("my2Email@mail.ru").build());
        User booker = userRepository.save(User.builder().id(0).name("Name1").email("my1Email@mail.ru").build());

        ItemRequest itemRequest = itemRequestRepository.save(new ItemRequest(
                0, "nn", firstBooker, LocalDateTime.now().minusHours(1)));
        Item item = itemRepository.save(new Item(1, "TV", "Wantching", true,
                owner, itemRequest));
        BookingDTOInput booking1 = new BookingDTOInput();
        booking1.setItemId(item.getId());
        booking1.setStart(LocalDateTime.now().plusSeconds(5));
        booking1.setEnd(LocalDateTime.now().plusSeconds(15));

        BookingDTOOutput booking = serviceBookingInBD.create(firstBooker.getId(), booking1);
        ItemDTO updateItem = new ItemDTO(item.getId(), "nn", "gete", false,
                userMapper.DTOUserFromUser(owner),
                null, null, null, itemRequest.getId());
        serviceItemInDB.update(owner.getId(), item.getId(), updateItem);
        Assertions.assertThrows(ValidationException.class,
                () -> serviceBookingInBD.create(booker.getId(), booking1));
    }

    @Test
    public void createTestWhenItemWantedToBookAgainByTheSameUser() {
        User owner = userRepository.save(User.builder().id(0).name("Name").email("myEmail@mail.ru").build());
        User firstBooker = userRepository.save(User.builder().id(0).name("Name2").email("my2Email@mail.ru").build());
        User booker = userRepository.save(User.builder().id(0).name("Name1").email("my1Email@mail.ru").build());

        ItemRequest itemRequest = itemRequestRepository.save(new ItemRequest(
                0, "nn", firstBooker, LocalDateTime.now().minusHours(1)));
        Item item = itemRepository.save(new Item(1, "TV", "Wantching", true,
                owner, itemRequest));
        BookingDTOInput booking1 = new BookingDTOInput();
        booking1.setItemId(item.getId());
        booking1.setStart(LocalDateTime.now().plusSeconds(5));
        booking1.setEnd(LocalDateTime.now().plusSeconds(15));

        BookingDTOOutput booking = serviceBookingInBD.create(firstBooker.getId(), booking1);
        BookingDTOOutput booking2 = serviceBookingInBD.create(firstBooker.getId(), booking1);
        Assertions.assertNotNull(booking);
        Assertions.assertNotNull(booking2);
    }
}