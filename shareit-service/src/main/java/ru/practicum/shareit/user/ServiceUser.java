package ru.practicum.shareit.user;

import java.util.List;

public interface ServiceUser {
    UserDTO create(UserDTO user);

    UserDTO update(int userId, UserDTO user);

    List<UserDTO> getUsers();

    UserDTO getUserById(int userId);

    void deleteById(int userId);
}
