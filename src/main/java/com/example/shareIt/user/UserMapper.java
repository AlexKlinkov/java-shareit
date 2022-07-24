package com.example.shareIt.user;

import lombok.Data;

@Data
public class UserMapper {

    public static UserDTO toUserDTO (User user) {
        return new UserDTO(user.getId(), user.getName(), user.getEmail());
    }

}
