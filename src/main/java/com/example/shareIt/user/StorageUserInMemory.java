package com.example.shareIt.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Component("StorageUserInMemory")
public class StorageUserInMemory implements StorageUser {
    private Map<Integer, User> mapOfUsers = new HashMap<>();
    private int id = 0;


    public User create (User user) {
        log.debug("Выкидываем внутренюю ошибку сервера при создании уже существующего пользователя");
        if (mapOfUsers.containsValue(user)) {
            throw new RuntimeException("Внутреняя ошибка сервера при создании уже существующего пользователя");
        } else {
            log.debug("Создаем пользователя если такого пользователя еще нет");
            id += 1;
            user.setId(id);
            mapOfUsers.put(id, user);
            return mapOfUsers.get(id);
        }
    }

    public User update (int userId, User user) {
        log.debug("Обновляем пользователя по ID, если такой пользователь существует");
        user.setId(userId);
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(mapOfUsers.get(userId).getName());
        }
        log.debug("Проверяем существует ли уже такой почтовый адрес в БД при обновлении пользователя");
        Map<String, Integer> emailAndIdOfUser = new HashMap<>();
        List<User> users = new ArrayList<>(mapOfUsers.values());
        for (User user1 : users) {
            emailAndIdOfUser.put(user1.getEmail(), user1.getId());
        }
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            Integer idOfUserWithThisEmail = emailAndIdOfUser.get(user.getEmail());
            if (idOfUserWithThisEmail != null) {
                if (idOfUserWithThisEmail != userId) {
                    throw new RuntimeException("Внутреняя ошибка сервера при обновлении пользователя (такой email уже существует)");
                }
            }
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            user.setEmail(mapOfUsers.get(userId).getEmail());
        }
        mapOfUsers.put(userId, user);
        return mapOfUsers.get(userId);
    }

    public List<User> getUsers () {
        return new ArrayList<>(mapOfUsers.values());
    }

    public User getUserById (int userId) {
        return mapOfUsers.get(userId);
    }

    public void deleteById (int userId) {
        mapOfUsers.remove(userId);
    }


    public User getUserWithoutListOfItems (User user) {
        User returnUser = new User(user.getId(), user.getName(), user.getEmail(), null);
        return returnUser;
    }
}
