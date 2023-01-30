package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.errorHandlerException.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Component("ServiceUserInBD")
@RequiredArgsConstructor
public class ServiceUserInBD implements ServiceUser {
    private final UserRepository userRepository;
    @Autowired
    private UserMapper userMapper = new UserMapperImpl();

    @Override
    public UserDTO create(UserDTO user) {
        return userMapper.DTOUserFromUser(userRepository.save(userMapper.userFromDTOUser(user)));
    }

    @Override
    public UserDTO update(int userId, UserDTO user) {
        log.debug("Берем предыдущиего пользователя из бд по id для обновления");
        User existUserInBD = userMapper.userFromDTOUser(getUserById(userId));
        if (user.getEmail() != null) {
            existUserInBD.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            existUserInBD.setName(user.getName());
        }
        log.debug("Обновляем пользователя");
        userRepository.save(existUserInBD);
        return getUserById(userId);
    }

    @Override
    public List<UserDTO> getUsers() {
        List<User> users = new ArrayList<>(userRepository.findAll());
        List<UserDTO> returnList = new ArrayList<>();
        for (User user : users) {
            returnList.add(userMapper.DTOUserFromUser(user));
        }
        return returnList;
    }

    @Override
    public UserDTO getUserById(int userId) {
        try {
            return userMapper.DTOUserFromUser(userRepository.getById(userId));
        } catch (Exception e) {
            throw new NotFoundException("Пользователя с таким id нет в БД");
        }
    }

    @Override
    public void deleteById(int userId) {
        userRepository.deleteById(userId);
    }
}