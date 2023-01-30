package ru.practicum.shareit.request;

import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-01-30T00:02:02+0300",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 11.0.15 (Amazon.com Inc.)"
)
public class ItemRequestMapperImpl implements ItemRequestMapper {

    @Override
    public ItemRequest itemRequestFromItemRequestDTOInput(ItemRequestDTOInput itemRequestDTOInput, Integer userId) {
        if ( itemRequestDTOInput == null && userId == null ) {
            return null;
        }

        ItemRequest itemRequest = new ItemRequest();

        if ( itemRequestDTOInput != null ) {
            itemRequest.setId( itemRequestDTOInput.getId() );
            itemRequest.setDescription( itemRequestDTOInput.getDescription() );
        }

        return itemRequest;
    }

    @Override
    public ItemRequestDTOOutput itemRequestDTOOutPutFromItemRequest(ItemRequest itemRequest, Integer userId) {
        if ( itemRequest == null && userId == null ) {
            return null;
        }

        ItemRequestDTOOutput itemRequestDTOOutput = new ItemRequestDTOOutput();

        if ( itemRequest != null ) {
            itemRequestDTOOutput.setId( itemRequest.getId() );
            itemRequestDTOOutput.setDescription( itemRequest.getDescription() );
            itemRequestDTOOutput.setRequestor( itemRequest.getRequestor() );
            itemRequestDTOOutput.setCreated( itemRequest.getCreated() );
        }

        return itemRequestDTOOutput;
    }
}
