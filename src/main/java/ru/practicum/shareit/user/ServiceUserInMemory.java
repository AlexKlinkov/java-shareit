package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Component("ServiceUserInMemory")
public class ServiceUserInMemory implements ServiceUser {
    private Map<Integer, User> userRepository = new HashMap<>();
    private int id = 0;
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);


    public UserDTO create (UserDTO user) {
        log.debug("Преобразовываем ДТО объект пришедший от пользователя в DAO объект, который работает с хранилищем, " +
                "в нашем случае в обычного user, при создании нового пользователя");
        User userFromDTO = userMapper.userFromDTOUser(user);
        log.debug("Выкидываем внутренюю ошибку сервера при создании уже существующего пользователя");
        if (userRepository.containsValue(userFromDTO)) {
            throw new RuntimeException("Внутреняя ошибка сервера при создании уже существующего пользователя");
        } else {
            log.debug("Создаем пользователя если такого пользователя еще нет");
            id += 1;
            userFromDTO.setId(id);
            userRepository.put(id, userFromDTO);
            log.debug("Возвращаем при создании нового пользователя тоже DTOUser");
            return userMapper.DTOUserFromUser(userRepository.get(id));
        }
    }

    public UserDTO update (int userId, UserDTO user) {
        log.debug("Преобразовываем ДТО объект пришедший от пользователя в DAO объект, который работает с хранилищем, " +
                "в нашем случае в обычного user, при обновлении уже существующего пользователя");
        user.setId(userId);
        User userFromDTO = userMapper.userFromDTOUser(user);
        log.debug("Обновляем пользователя по ID, если такой пользователь существует");
        if (userFromDTO.getName() == null || userFromDTO.getName().isEmpty()) {
            userFromDTO.setName(userRepository.get(userId).getName());
        }
        log.debug("Проверяем существует ли уже такой почтовый адрес в БД при обновлении пользователя");
        Map<String, Integer> emailAndIdOfUser = new HashMap<>();
        List<User> users = new ArrayList<>(userRepository.values());
        for (User user1 : users) {
            emailAndIdOfUser.put(user1.getEmail(), user1.getId());
        }
        if (userFromDTO.getEmail() != null && !userFromDTO.getEmail().isEmpty()) {
            Integer idOfUserWithThisEmail = emailAndIdOfUser.get(userFromDTO.getEmail());
            if (idOfUserWithThisEmail != null) {
                if (idOfUserWithThisEmail != userId) {
                    throw new RuntimeException("Внутреняя ошибка сервера при обновлении пользователя (такой email уже существует)");
                }
            }
        }
        if (userFromDTO.getEmail() == null || userFromDTO.getEmail().isEmpty()) {
            userFromDTO.setEmail(userRepository.get(userId).getEmail());
        }
        userRepository.put(userId, userFromDTO);
        log.debug("Возвращаем при обновлении уже существующего пользователя тоже DTOUser");
        return userMapper.DTOUserFromUser(userRepository.get(userId));
    }

    public List<UserDTO> getUsers () {
        List<UserDTO> listWithUsersForReturn = new ArrayList<>();
        List<User> users = new ArrayList<>(userRepository.values());
        for (User user : users) {
            listWithUsersForReturn.add(userMapper.DTOUserFromUser(user));
        }
        return listWithUsersForReturn;
    }

    public UserDTO getUserById (int userId) {
        return userMapper.DTOUserFromUser(userRepository.get(userId));
    }

    public void deleteById (int userId) {
        userRepository.remove(userId);
    }
}
