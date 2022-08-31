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
import ru.practicum.shareit.errorHandlerException.MyMethodArgumentTypeMismatchException;
import ru.practicum.shareit.errorHandlerException.NotFoundException;
import ru.practicum.shareit.errorHandlerException.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
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
    private BookingDTOInput booking;

    @BeforeEach
    public void init () {
        booking = new BookingDTOInput();
        booking.setStart(LocalDateTime.now().plusSeconds(15));
        booking.setEnd(LocalDateTime.now().plusSeconds(30));
        booking.setItemId(1);
    }

    @Test
    public void createBookingTestWhenUserIsEmpty () {
        Assertions.assertThrows(NotFoundException.class,
                () -> serviceBookingInBD.create(1, booking));
    }

    @Test
    public void createBookingTestWhenItemIsEmpty () {
        userRepository.save(User.builder().id(1).name("Petre").email("Petre@mail.ru").build());
        Assertions.assertThrows(NotFoundException.class,
                () -> serviceBookingInBD.create(1, booking));
    }

    @Test
    public void createBookingTestWhenOwnerTryBooking () {
        User owner = userRepository.save(User.builder().id(2).name("Petre").email("Petre@mail.ru").build());
        itemRepository.save(
                new Item(1,
                        "tool1", "tool1Description", true, owner, null));
        Assertions.assertThrows(NotFoundException.class,
                () -> serviceBookingInBD.create(2, booking));
    }

    @Test
    public void createBookingTestWhenItemIsNotAvailable () {
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
        serviceBookingInBD.create(booker.getId(), booking);
        bookingRepository.getById(6).setStatus(TypeOfStatus.APPROVED);
        Assertions.assertThrows(ValidationException.class,
                () -> serviceBookingInBD.updateStatusOfBooking(owner.getId(), 6, true));
    }

    @Test
    public void updateStatusBookingTestWhenTryUpdateNotOwner() {
        User owner = userRepository.save(User.builder().id(1).name("Petre").email("Petre@mail.ru").build());
        User booker = userRepository.save(User.builder().id(2).name("Booker").email("Booker@mail.ru").build());
        itemRepository.save(
                new Item(3,
                        "tool1", "tool1Description", true, owner, null));
        booking.setItemId(3);
        serviceBookingInBD.create(booker.getId(), booking);
        Assertions.assertThrows(NotFoundException.class,
                () -> serviceBookingInBD.updateStatusOfBooking(booker.getId(), 3, true));
    }

    @Test
    public void updateStatusBookingTestWhenIsOk() {
        User owner = userRepository.save(User.builder().id(7).name("Petre").email("Petre@mail.ru").build());
        User booker = userRepository.save(User.builder().id(8).name("Booker").email("Booker@mail.ru").build());
        Item item = itemRepository.save(
                new Item(4,
                        "tool1", "tool1Description", true, owner, null));
        booking.setItemId(item.getId());
        serviceBookingInBD.create(booker.getId(), booking);
        BookingDTOOutput bookingDTOOutput = serviceBookingInBD.updateStatusOfBooking(owner.getId(), 5, true);
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
        BookingDTOOutput bookingDTOOutput = serviceBookingInBD.updateStatusOfBooking(owner.getId(), 1, false);
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
        itemRepository.save(new Item(2 , "secondItem", "Description", true, owner, null));
        booking.setItemId(2);
        serviceBookingInBD.create(booker.getId(), booking);
        Assertions.assertNotNull(serviceBookingInBD.findBookingByIdAndUserId(booker.getId(), 2));
    }

    @Test
    public void getBookingsByBookerIdOrOwnerIdWhenStatusIsNotCorrect() {
        Assertions.assertThrows(MyMethodArgumentTypeMismatchException.class,
                () -> serviceBookingInBD.getBookingsByOwnerIdOrBookingID(0, "StatusNotCorrect",
                        0,1,"booker"));
    }

    @Test
    public void getBookingsByBookerIdOrOwnerIdWhenUserIsNotExist() {
        Assertions.assertThrows(NotFoundException.class,
                () -> serviceBookingInBD.getBookingsByOwnerIdOrBookingID(10000, "ALL",
                        0,1,"booker"));
    }

    @Test
    public void getBookingsByBookerIdOrOwnerIdWhenALLIsOK() {
        User user = userRepository.save(User.builder().id(4).name("bookerFourth").email("Fourth@mail.ru").build());
        Assertions.assertEquals(0,
                serviceBookingInBD.getBookingsByOwnerIdOrBookingID(user.getId(), "ALL",
                        0,1,"").size());
    }

    @Test
    public void getBookingsByBookerIdOrOwnerIdWhenFUTUREBooking() {
        User booker = userRepository.save(User.builder().id(1).name("Booker").email("Booker@mail.ru").build());
        Assertions.assertEquals(0,
                serviceBookingInBD.getBookingsByOwnerIdOrBookingID(booker.getId(), "FUTURE",
                        0,1,"").size());
    }

    @Test
    public void getBookingsByBookerIdOrOwnerIdWhenCURRENTBooking() {
        User user = userRepository.save(User.builder().id(1).name("Name").email("email@email").build());
        Assertions.assertEquals(0,
                serviceBookingInBD.getBookingsByOwnerIdOrBookingID(user.getId(), "CURRENT",
                        0,1,"").size());
    }

    @Test
    public void getBookingsByBookerIdOrOwnerIdWhenPASTBooking() {
        User user = userRepository.save(User.builder().id(4).name("bookerFourth").email("Fourth@mail.ru").build());
        Assertions.assertEquals(0,
                serviceBookingInBD.getBookingsByOwnerIdOrBookingID(user.getId(), "PAST",
                        0,1,"").size());
    }

    @Test
    public void getBookingsByBookerIdOrOwnerIdWhenWATINGBooking() {
        User user = userRepository.save(User.builder().id(4).name("bookerFourth").email("Fourth@mail.ru").build());
        Assertions.assertEquals(0,
                serviceBookingInBD.getBookingsByOwnerIdOrBookingID(user.getId(), "WAITING",
                        0,1,"").size());
    }

    @Test
    public void getBookingsByBookerIdOrOwnerIdWhenREJECTBookingBOOKER() {
        User booker = userRepository.save(User.builder().id(1).name("Booker").email("Booker@mail.ru").build());
        Assertions.assertEquals(0,
                serviceBookingInBD.getBookingsByOwnerIdOrBookingID(booker.getId(), "REJECTED",
                        0,1, "booker").size());
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
                        0,1,"owner").size());
    }

    @Test
    public void getBookingsByBookerIdOrOwnerIdTestWhenStartAndFinishEquels() {
        User user = userRepository.save(User.builder().id(0).name("Name").email("Name@mail.ru").build());
        Assertions.assertThrows(ValidationException.class,
                () -> serviceBookingInBD.getBookingsByOwnerIdOrBookingID(user.getId(), "ALL",1, 0, ""));
    }

    @Test
    public void getBookingsByBookerIdOrOwnerIdTestWhenStartNegativeOrAmountIsNegative() {
        User user = userRepository.save(User.builder().id(0).name("Name").email("Name@mail.ru").build());
        Assertions.assertThrows(ValidationException.class,
                () -> serviceBookingInBD.getBookingsByOwnerIdOrBookingID(user.getId(), "ALL",-1, 1, ""));
    }

/*    @Test
    public void getBookingDTOOutputsTest1() {
        Page<Booking> page;
        Assertions.assertNotNull(serviceBookingInBD.getBookingDTOOutputs(page, 1, 2, 3));
    }*/
}
