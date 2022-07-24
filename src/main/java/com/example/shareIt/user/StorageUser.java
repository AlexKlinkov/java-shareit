package com.example.shareIt.user;

import java.util.List;

public interface StorageUser {
    User create (User user);
    User update (int userId, User user);
    List<User> getUsers();
    User getUserById (int userId);
    void deleteById (int userId);
    User getUserWithoutListOfItems (User user);
}
