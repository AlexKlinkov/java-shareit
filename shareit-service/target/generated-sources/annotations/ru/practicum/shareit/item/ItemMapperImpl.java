package ru.practicum.shareit.item;

import javax.annotation.processing.Generated;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDTO;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-01-30T00:02:02+0300",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 11.0.15 (Amazon.com Inc.)"
)
public class ItemMapperImpl implements ItemMapper {

    @Override
    public Item itemFromItemDTO(ItemDTO item) {
        if ( item == null ) {
            return null;
        }

        Item item1 = new Item();

        item1.setId( item.getId() );
        item1.setName( item.getName() );
        item1.setDescription( item.getDescription() );
        item1.setAvailable( item.getAvailable() );
        item1.setOwner( userDTOToUser( item.getOwner() ) );

        return item1;
    }

    @Override
    public ItemDTO itemDTOFromItem(Item item) {
        if ( item == null ) {
            return null;
        }

        ItemDTO itemDTO = new ItemDTO();

        if ( item.getId() != null ) {
            itemDTO.setId( item.getId() );
        }
        itemDTO.setName( item.getName() );
        itemDTO.setDescription( item.getDescription() );
        itemDTO.setAvailable( item.getAvailable() );
        itemDTO.setOwner( userToUserDTO( item.getOwner() ) );

        return itemDTO;
    }

    protected User userDTOToUser(UserDTO userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.id( userDTO.getId() );
        user.name( userDTO.getName() );
        user.email( userDTO.getEmail() );

        return user.build();
    }

    protected UserDTO userToUserDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setId( user.getId() );
        userDTO.setName( user.getName() );
        userDTO.setEmail( user.getEmail() );

        return userDTO;
    }
}
