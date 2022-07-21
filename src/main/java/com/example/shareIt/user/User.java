package com.example.shareIt.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
public class User {
    transient int id;
    @NotNull
    @Pattern(regexp = "^\\S*$")
    String name;
    @Email
    String email;
}
