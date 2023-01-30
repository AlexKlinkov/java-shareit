package ru.practicum.shareit.item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingDTO;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.comment.*;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserDTO;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserMapperImpl;
import ru.practicum.shareit.user.UserRepository;

@Slf4j
@Component("ItemMapperInitialization")
public class ItemMapperInitialization implements ItemMapper {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired

    private CommentMapper commentMapper = new CommentMapperInitialization(getUserRepository(), getItemRepository());
    @Autowired
    private UserMapper userMapper = new UserMapperImpl();


    @Override
    public Item itemFromItemDTO(ItemDTO item) {
        if (item == null) {
            return null;
        }

        Item item1 = new Item();

        item1.setId(item.getId());
        item1.setName(item.getName());
        item1.setDescription(item.getDescription());
        item1.setAvailable(item.getAvailable());
        item1.setOwner(userMapper.userFromDTOUser(item.getOwner()));
        log.debug("В маппере вещи получаем информацию о запросе");
        if (item.getRequestId() != null) {
            item1.setRequest(itemRequestRepository.getById(item.getRequestId()));
        } else {
            item1.setRequest(null);
        }
        return item1;
    }

    @Override
    public ItemDTO itemDTOFromItem(Item item) {
        if (item == null) {
            return null;
        }

        int id = 0;
        String name = null;
        String description = null;
        Boolean available = null;
        UserDTO owner = null;
        Integer requestId = null;

        if (item.getId() != null) {
            id = item.getId();
        }
        name = item.getName();
        description = item.getDescription();
        available = item.getAvailable();
        owner = userMapper.DTOUserFromUser(userRepository.getById(item.getOwner().getId()));
        if (item.getRequest() != null) {
            requestId = item.getRequest().getId();
        }
        log.debug("В маппере вещи определяем последнее и следующее бронирование");
        LocalDateTime now = LocalDateTime.now(); // Текущее время
        log.debug("В маппере вещи получаем список будующих бронирований (nextBooking)");
        List<Booking> bookingsInFuture = bookingRepository.findBookingsByItemIdAndItemOwnerIdAndStartAfter(
                item.getId(), item.getOwner().getId(), now);
        log.debug("Проверяем в маппере вещи, что бронирование на будущие даты данной вещи существует");
        BookingDTO nextBooking;
        if (!bookingsInFuture.isEmpty()) {
            Booking firstFutureBooking = bookingsInFuture.get(0); // nextBooking
            nextBooking = new BookingDTO(
                    firstFutureBooking.getId(),
                    firstFutureBooking.getBooker().getId(),
                    firstFutureBooking.getStart(),
                    firstFutureBooking.getEnd());
        } else {
            nextBooking = null;
        }
        log.debug("В маппере вещи получаем список прошлых бронирований (lastBooking)");
        List<Booking> bookingsInPast =
                new ArrayList<>(bookingRepository.findBookingsByItemIdAndItemOwnerIdAndEndBefore(
                        item.getId(), item.getOwner().getId(), now));
        log.debug("Проверяем в маппере вещи, что есть бронирования на прошедшие даты данной вещи");
        BookingDTO lastBooking;
        if (!bookingsInPast.isEmpty()) {
            Booking lastBookingInPast = bookingsInPast.get(bookingsInPast.size() - 1); // lastBooking
            lastBooking = new BookingDTO(
                    lastBookingInPast.getId(),
                    lastBookingInPast.getBooker().getId(),
                    lastBookingInPast.getStart(),
                    lastBookingInPast.getEnd());
        } else {
            lastBooking = null;
        }
        List<Comment> allComments = commentRepository.findAllByItemId(item.getId());
        List<CommentDTOOutput> comments = new ArrayList<>();
        if (!allComments.isEmpty()) {
            for (Comment comment : allComments) {
                comments.add(commentMapper.commentDTOOutputFromComment(comment));
            }
        }

        ItemDTO itemDTO = new ItemDTO(id, name, description, available, owner,
                lastBooking, nextBooking, comments, requestId);
        log.debug("В маппере вещей все прошло успешно");
        return itemDTO;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public ItemRepository getItemRepository() {
        return itemRepository;
    }
}