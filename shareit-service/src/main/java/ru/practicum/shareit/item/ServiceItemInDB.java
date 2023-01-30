package ru.practicum.shareit.item;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.TypeOfStatus;
import ru.practicum.shareit.errorHandlerException.NotFoundException;
import ru.practicum.shareit.errorHandlerException.ValidationException;
import ru.practicum.shareit.item.comment.*;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserMapperImpl;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@Component("ServiceItemInDB")
@Data
@RequiredArgsConstructor
public class ServiceItemInDB implements ServiceItem {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserMapperImpl userMapper = new UserMapperImpl();

    @Autowired
    private CommentMapperInitialization commentMapper = new CommentMapperInitialization(getUserRepository(),
            getItemRepository());
    @Autowired
    private ItemMapperInitialization itemMapper = new ItemMapperInitialization();

    @Override
    public ItemDTO create(int ownerId, ItemDTO item) {
        if (checkAlreadyExistItem(item)) {
            throw new RuntimeException("Внутреняя ошибка сервера при создании уже существующей вещи");
        }
        log.debug("При создании вещи определяем ее хозяина");
        item.setOwner(userMapper.DTOUserFromUser(userRepository.getById(ownerId)));
        log.debug("Сохраняем новую вещь в БД");
        Item itemReturn = itemRepository.save(itemMapper.itemFromItemDTO(item));
        return itemMapper.itemDTOFromItem(itemReturn);
    }

    @Override
    public ItemDTO update(int ownerId, int itemId, ItemDTO item) {
        log.debug("Получаем ту вещь из бд которую нужно обновить");
        ItemDTO previousItem = getItemById(ownerId, itemId);
        log.debug("Проверяем, что хозяин вещи обновляет свою вещь, а не чью-то чужую");
        if (previousItem.getOwner().getId() != ownerId) {
            throw new NotFoundException("Пользователь пытается обновить данные по вещи, которая ему не принадлежит");
        }
        log.debug("Оставляем значение полей от предущей вещи, если у новой они пустые или null при обновлении");
        if (item.getDescription() == null) {
            log.debug("Значение описания при обновлении от предыдущей версии вещи");
            item.setDescription(previousItem.getDescription());
        }
        if (item.getName() == null) {
            log.debug("Значение имени при обновлении от предыдущей версии вещи");
            item.setName(previousItem.getName());
        }
        if (item.getOwner() == null) {
            log.debug("Значение собственника при обновлении от предыдущей версии вещи");
            item.setOwner(getItemById(ownerId, itemId).getOwner());
        }
        if (item.available == null) {
            item.available = previousItem.available;
        }
        if (item.getRequestId() == null) {
            log.debug("Значение запроса при обновлении от предыдущей версии вещи");
            item.setRequestId(previousItem.getRequestId());
        }
        log.debug("Обновляем вещь");
        item.setId(itemId);
        itemRepository.save(itemMapper.itemFromItemDTO(item));
        return getItemById(ownerId, itemId);
    }

    @Override
    public List<ItemDTO> getItems(int ownerId) {
        List<Item> items = new ArrayList<>(itemRepository.findAll());
        List<ItemDTO> returnList = new ArrayList<>();
        for (Item item : items) {
            if (item.getOwner().getId() == ownerId) {
                returnList.add(itemMapper.itemDTOFromItem(item));
            }
        }
        returnList.sort(Comparator.comparing(ItemDTO::getId));
        return returnList;
    }

    @Override
    public ItemDTO getItemById(int userId, int itemId) {
        try {
            log.debug("Получаем вещь из бд при попытке вернуть вещь по ID");
            Item item = itemRepository.getById(itemId);
            log.debug("Скрываем информацию о бронировании от не владельца вещи, " +
                    "обнуляя поля lastBooking и nextBooking");
            ItemDTO itemDTO = itemMapper.itemDTOFromItem(item);
            if (item.getOwner().getId() != userId) {
                itemDTO.setLastBooking(null);
                itemDTO.setNextBooking(null);
            }
            return itemDTO;
        } catch (Exception e) {
            throw new NotFoundException("Вещи с таким id нет в БД");
        }
    }

    @Override
    public List<ItemDTO> getItemBySearchText(String text) {
        log.debug("Возвращаем список вещей доступных для аренды по поиску 'ключевому слово'");
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        List<ItemDTO> returnAllFoundItemsByText = new ArrayList<>();
        List<Item> allItems = new ArrayList<>(itemRepository.findAll());
        for (Item item : allItems) {
            if (item.getAvailable()) {
                if (item.getName().toUpperCase().contains(text.toUpperCase()) ||
                        item.getDescription().toUpperCase().contains(text.toUpperCase())) {
                    returnAllFoundItemsByText.add(itemMapper.itemDTOFromItem(item));
                }
            }
        }
        return returnAllFoundItemsByText;
    }

    public boolean checkAlreadyExistItem(ItemDTO item) {
        List<Item> items = new ArrayList<>(itemRepository.findAll());
        for (Item itemInBD : items) {
            if (itemInBD.getName().equals(item.getName()) &&
                    itemInBD.getDescription().equals(item.getDescription())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public CommentDTOOutput addComment(int userId, int itemId, CommentDTOInput commentDTOInput) {
        log.debug("Пользователь, который брал вещь в пользование может оставить комментарий, " +
                "Проверяем, что текст комменатрия не пустой");
        LocalDateTime now = LocalDateTime.now();
        if (commentDTOInput.getText().isEmpty()) {
            throw new ValidationException("Комменатрий не может быть пустым");
        }
        log.debug("Пытаемся получить бронирование конкретной вещи, конкретным пользователем для того" +
                "чтоб оставить комментарий");
        Booking booking = bookingRepository.findBookingByBooker_IdAndItemIdAndEndBeforeAndStatusIs(
                userId, itemId, now, TypeOfStatus.APPROVED);
        if (booking == null) {
            throw new ValidationException("Данный пользователь не брал эту вещь в аренду, " +
                    "поэтому и не может оставлять комментарии");
        }
        log.debug("Сохраняем комментарий в БД");
        Comment commentInBD =
                commentRepository.save(commentMapper.commentFromCommentDTOInput(userId, itemId, commentDTOInput));
        return commentMapper.commentDTOOutputFromComment(commentInBD);

    }

    @Override
    public ItemDTO getItemDTOByRequestId(Integer requestId) {
        log.debug("Получаем список всех вещей, чтоб потом вернуть вещь по requestID");
        List<Item> allItems = new ArrayList<>(itemRepository.findAll());
        for (Item item : allItems) {
            if (itemMapper.itemDTOFromItem(item).getRequestId() == requestId) {
                return itemMapper.itemDTOFromItem(item);
            }
        }
        return null;
    }
}