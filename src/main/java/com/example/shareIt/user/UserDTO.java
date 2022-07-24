package com.example.shareIt.user;

import lombok.Data;

@Data
public class UserDTO {
    int id;
    String name;
    String email;

    public UserDTO (int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
