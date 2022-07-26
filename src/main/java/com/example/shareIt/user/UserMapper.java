package com.example.shareIt.user;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface UserMapper {

    User userFromDTOUser (UserDTO user);
    UserDTO DTOUserFromUser (User user);
}
