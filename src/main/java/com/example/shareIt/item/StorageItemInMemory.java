package com.example.shareIt.item;

import com.example.shareIt.errorHandlerException.NotFoundException;
import com.example.shareIt.errorHandlerException.ValidationException;
import com.example.shareIt.user.StorageUser;
import com.example.shareIt.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@Component("StorageItemInMemory")
public class StorageItemInMemory implements StorageItem{
    private Map<Integer, ItemDTO> mapOfItems = new HashMap<>();
    private int id = 0;
    private StorageUser storageUser;

    @Autowired
    public StorageItemInMemory(@Qualifier("StorageUserInMemory") StorageUser storageUser) {
        this.storageUser = storageUser;
    }

    public ItemDTO create (int ownerId, ItemDTO item) {
        log.debug("Не может быть создана новая вещь без указания владельца этой вещи");
        User newUser = storageUser.getUserById(ownerId);
        if (newUser == null) {
            throw new NotFoundException("Искомый объект при созднии вещи не найден");
        }
        log.debug("Выкидываем внутренюю ошибку сервера при создании уже существующей вещи");
        if (mapOfItems.containsValue(item)) {
            throw new RuntimeException("Внутреняя ошибка сервера при создании уже существующей вещи");
        } else {
            log.debug("Создаем вещь если такой вещи еще нет");
            id += 1;
            item.setId(id);
            log.debug("При создании вещи, добавляем ее в список вещей пользователя");
            if (newUser.getListWithAllItemsWhichBelongsOwner() == null) {
                newUser.setListWithAllItemsWhichBelongsOwner(new ArrayList<>());
                newUser.getListWithAllItemsWhichBelongsOwner().add(item);
            } else {
                newUser.getListWithAllItemsWhichBelongsOwner().add(item);
            }
            log.debug("Обновляем пользователя в хранилище мапе пользователей");
            storageUser.update(ownerId, newUser);
            // Избегаем цикличности, получаем вещь и ее владельца (без списка вещей, которые принаджежат владельцу)
            // Один пользователь для хранилища пользователей ( со списком вещей ему принадлежащих)
            // Другой пользователь для хранилища веще, для поля владелец
            item.setOwner(storageUser.getUserWithoutListOfItems(newUser));
            log.debug("Помещаем созданную вещи в мапу хранилище");
            mapOfItems.put(id, item);
            System.out.println(storageUser.getUserById(ownerId).getListWithAllItemsWhichBelongsOwner());
            return mapOfItems.get(id);
            }
        }

    public ItemDTO update (int ownerId, int itemId, ItemDTO item) {
        log.debug("Проверяем, что при обновлении вещи был указан ID ее хозяина");
        if ((Integer) ownerId == null) {
            throw new ValidationException("Не указан хозяин обновляемой вещи");
        }
        log.debug("Проверяем, что хозяин вещи обновляет свою вещь, а не чью-то чужую");
        if (getItemById(itemId).getOwner().getId() != ownerId) {
            throw new NotFoundException("Пользователь пытается обновить данные по вещи, которая ему не принадлежит");
        }
        log.debug("Обновляем вещь по ID, если такая вещь уже существует");
        if (mapOfItems.containsKey(itemId)) {
            item.setId(itemId);
            log.debug("Оставляем значение полей от предущей вещи, если у новой они пустые или null при обновлении");
            ItemDTO previousItem = getItemById(itemId);
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
                item.setOwner(previousItem.getOwner());
            }
            if (item.getRequest() == null) {
                log.debug("Значение запроса при обновлении от предыдущей версии вещи");
                item.setRequest(previousItem.getRequest());
            }
            log.debug("Обновляем вещь в списке вещей пользователя");
            User user = storageUser.getUserById(ownerId);
            System.out.println(user.getListWithAllItemsWhichBelongsOwner());
            user.getListWithAllItemsWhichBelongsOwner().remove(previousItem);
            user.getListWithAllItemsWhichBelongsOwner().add(item);
            storageUser.update(ownerId, user);
            mapOfItems.put(id, item);
            return mapOfItems.get(id);
        }
        return null;
    }


    public List<ItemDTO> getItems (int ownerId) {
        return  storageUser.getUserById(ownerId).getListWithAllItemsWhichBelongsOwner();
    }

    public ItemDTO getItemById (int itemId) {
/*        List<List<ItemDTO>> allItems = new ArrayList<>(mapOfItems.values());
        List<ItemDTO> listWithAllItems = new ArrayList<>();
        for (List<ItemDTO> list : allItems) {
            for (ItemDTO item : list) {
                listWithAllItems.add(item);
            }
        }
        for (ItemDTO itemResult : listWithAllItems) {
            if (itemResult.getId() == itemId) {
                return itemResult;
            }
        }
        return null;*/
        return mapOfItems.get(itemId);
    }


    public ItemDTO getItemFromListWithItemsOfOwnerByOwnerIdAndItemId (int ownerId, int itemId) {
        List<ItemDTO> items = storageUser.getUserById(ownerId).getListWithAllItemsWhichBelongsOwner();
        for (ItemDTO item : items) {
            if (item.getId() == itemId) {
                ItemDTO returnItem = storageUser.getUserById(ownerId).getListWithAllItemsWhichBelongsOwner().get(
                        new ArrayList<>(items).indexOf(item));
                return returnItem;
            }
        }
        return null;
    }
}
