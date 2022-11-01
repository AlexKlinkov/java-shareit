package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final ServiceUser storageUser;

    @Autowired
    public UserController(@Qualifier("ServiceUserInBD") ServiceUser storageUser) {
        this.storageUser = storageUser;
    }


    @PostMapping()
    public UserDTO create(@Valid @RequestBody UserDTO user) {
        return storageUser.create(user);
    }

    @PatchMapping(path = "/{userId}")
    public UserDTO update(@PathVariable int userId, @RequestBody UserDTO user) {
        return storageUser.update(userId, user);
    }

    @GetMapping
    public List<UserDTO> getUsers() {
        return storageUser.getUsers();
    }

    @GetMapping(path = "/{userId}")
    public UserDTO getUserById(@PathVariable int userId) {
        return storageUser.getUserById(userId);
    }

    @DeleteMapping(path = "/{userId}")
    public void deleteById(@PathVariable int userId) {
        storageUser.deleteById(userId);
    }
}