package com.example.shareIt.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final StorageUser storageUser;

    @Autowired
    public UserController(@Qualifier("StorageUserInMemory") StorageUser storageUser) {
        this.storageUser = storageUser;
    }


    @PostMapping()
    public User create (@Valid @RequestBody User user) {
        return storageUser.create(user);
    }

    @PatchMapping(path = "/{userId}")
    public User update (@PathVariable int userId, @RequestBody User user) {
        return storageUser.update(userId, user);
    }

    @GetMapping
    public List<User> getUsers () {
        return storageUser.getUsers();
    }

    @GetMapping(path = "/{userId}")
    public User getUserById (@PathVariable int userId) {
        return storageUser.getUserById(userId);
    }

    @DeleteMapping(path = "/{userId}")
    public void deleteById (@PathVariable int userId) {
        storageUser.deleteById(userId);
    }
}
