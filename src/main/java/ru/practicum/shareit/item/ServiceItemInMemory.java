package ru.practicum.shareit.item;

import ru.practicum.shareit.errorHandlerException.NotFoundException;
import ru.practicum.shareit.errorHandlerException.ValidationException;
import ru.practicum.shareit.item.comment.CommentDTOInput;
import ru.practicum.shareit.item.comment.CommentDTOOutput;
import ru.practicum.shareit.user.*;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@Component("ServiceItemInMemory")
public class ServiceItemInMemory implements ServiceItem{
    private Map<Integer, Item> itemRepository = new HashMap<>();
    private int id = 0;
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private ServiceUser serviceUser;

    @Autowired
    public ServiceItemInMemory(@Qualifier("ServiceUserInMemory") ServiceUser serviceUser) {
        this.serviceUser = serviceUser;
    }

    public ItemDTO create (int ownerId, ItemDTO item) {
        log.debug("Преобразовываем ДТО объект пришедший от пользователя в DAO объект, который работает с хранилищем, " +
                "в нашем случае в обычный item, при создании новой вещи");
        Item itemFromDTO = itemMapper.itemFromItemDTO(item);
        log.debug("Не может быть создана новая вещь без указания владельца этой вещи");
        User newUser = userMapper.userFromDTOUser(serviceUser.getUserById(ownerId));
        if (newUser == null) {
            throw new NotFoundException("Искомый объект при созднии вещи не найден");
        }
        log.debug("Выкидываем внутренюю ошибку сервера при создании уже существующей вещи");
        if (itemRepository.containsValue(itemFromDTO)) {
            throw new RuntimeException("Внутреняя ошибка сервера при создании уже существующей вещи");
        } else {
            log.debug("Создаем вещь если такой вещи еще нет");
            id += 1;
            itemFromDTO.setId(id);
            log.debug("При создании вещи, добавляем ее в список вещей пользователя");
            if (newUser.getListWithAllItemsWhichBelongsOwner() == null) {
                newUser.setListWithAllItemsWhichBelongsOwner(new ArrayList<>());
                newUser.getListWithAllItemsWhichBelongsOwner().add(itemFromDTO);
            } else {
                newUser.getListWithAllItemsWhichBelongsOwner().add(itemFromDTO);
            }
            log.debug("Обновляем пользователя в хранилище мапе пользователей");
            UserDTO user = userMapper.DTOUserFromUser(newUser);
            serviceUser.update(ownerId, user);
            // Избегаем цикличности, получаем вещь и ее владельца (без списка вещей, которые принаджежат владельцу)
            user.setListWithAllItemsWhichBelongsOwner(null);
            itemFromDTO.setOwner(userMapper.userFromDTOUser(user));
            log.debug("Помещаем созданную вещи в мапу хранилище");
            itemRepository.put(id, itemFromDTO);
            log.debug("Возвращаем при создании новой вещи тоже DTOItem");
            return itemMapper.itemDTOFromItem(itemRepository.get(id));
        }
    }

    public ItemDTO update (int ownerId, int itemId, ItemDTO item) {
        log.debug("Проверяем, что при обновлении вещи был указан ID ее хозяина");
        if ((Integer) ownerId == null) {
            throw new ValidationException("Не указан хозяин обновляемой вещи");
        }
        log.debug("Проверяем, что хозяин вещи обновляет свою вещь, а не чью-то чужую");
        if (getItemById(ownerId, itemId).getOwner().getId() != ownerId) {
            throw new NotFoundException("Пользователь пытается обновить данные по вещи, которая ему не принадлежит");
        }
        log.debug("Обновляем вещь по ID, если такая вещь уже существует");
        if (itemRepository.containsKey(itemId)) {
            item.setId(itemId);
            log.debug("Оставляем значение полей от предущей вещи, если у новой они пустые или null при обновлении");
            Item previousItem = itemMapper.itemFromItemDTO(getItemById(ownerId, itemId));
            if (item.getName() == null || item.getName().isEmpty()) {
                log.debug("Значение имени при обновлении от предыдущей версии вещи");
                item.setName(previousItem.getName());
            }
            if (item.getDescription() == null || item.getDescription().isEmpty()) {
                log.debug("Значение описания при обновлении от предыдущей версии вещи");
                item.setDescription(previousItem.getDescription());
            }
            if (item.getAvailable() == null) {
                log.debug("Значение доступности при обновлении от предыдущей версии вещи");
                item.setAvailable(previousItem.getAvailable());
            }
            if (item.getOwner() == null) {
                log.debug("Значение собственника при обновлении от предыдущей версии вещи");
                item.setOwner(getItemById(ownerId, itemId).getOwner());
            }
            if (item.getRequestId() == null) {
                log.debug("Значение запроса при обновлении от предыдущей версии вещи");
                item.setRequestId(previousItem.getRequest().getId());
            }
            log.debug("Обновляем вещь в списке вещей пользователя");
            User user = userMapper.userFromDTOUser(serviceUser.getUserById(ownerId));
            previousItem.setOwner(null);
            List<Item> items = new ArrayList<>(user.getListWithAllItemsWhichBelongsOwner());
            for (Item itemDTO : items) {
                if (itemMapper.itemDTOFromItem(previousItem).getId() == itemDTO.getId()) {
                    items.remove(itemDTO);
                    items.add(itemMapper.itemFromItemDTO(item));
                    user.setListWithAllItemsWhichBelongsOwner(items);
                }
            }
            serviceUser.update(ownerId, userMapper.DTOUserFromUser(user));
            itemRepository.put(itemId, itemMapper.itemFromItemDTO(item));
            return itemMapper.itemDTOFromItem(itemRepository.get(itemId));
        }
        return null;
    }


    public List<ItemDTO> getItems (int ownerId) {
        return serviceUser.getUserById(ownerId).getListWithAllItemsWhichBelongsOwner();
    }

    public ItemDTO getItemById (int userId, int itemId) {
        return itemMapper.itemDTOFromItem(itemRepository.get(itemId));
    }

    public List<ItemDTO> getItemBySearchText (String text) {
        log.debug("Возвращаем список доступных вещей для аренды по поиску 'ключевому слово'");
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        List<ItemDTO> returnAllFoundItemsByText = new ArrayList<>();
        List<Item> allItems = new ArrayList<>(itemRepository.values());
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

    @Override
    public CommentDTOOutput addComment(int userId, int itemId, CommentDTOInput commentDTOInput) {
        return null;
    }

    @Override
    public ItemDTO getItemDTOByRequestId(Integer requestId) {
        return null;
    }
}