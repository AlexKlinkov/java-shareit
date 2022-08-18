package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemDTO;
import ru.practicum.shareit.item.ServiceItem;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component("ItemRequestMapperInitialization")
public class ItemRequestMapperInitialization implements ItemRequestMapper {

    private final UserRepository userRepository;
    private final ServiceItem serviceItem;

    @Autowired
    public ItemRequestMapperInitialization(UserRepository userRepository,
                                           @Qualifier("ServiceItemInDB") ServiceItem serviceItem) {
        this.userRepository = userRepository;
        this.serviceItem = serviceItem;
    }

    @Override
    public ItemRequest itemRequestFromItemRequestDTOInput(ItemRequestDTOInput itemRequestDTOInput, Integer userId) {
        if (itemRequestDTOInput == null) {
            return null;
        }

        ItemRequest itemRequest = new ItemRequest();

        itemRequest.setDescription(itemRequestDTOInput.getDescription());
        User requestor = new User();
        requestor.setId(userId);
        requestor.setName(userRepository.getById(userId).getName());
        requestor.setEmail(userRepository.getById(userId).getEmail());
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());

        return itemRequest;
    }

    @Override
    public ItemRequestDTOOutput itemRequestDTOOutPutFromItemRequest(ItemRequest itemRequest, Integer userId) {
        if (itemRequest == null) {
            return null;
        }

        ItemRequestDTOOutput itemRequestDTOOutput = new ItemRequestDTOOutput();

        itemRequestDTOOutput.setId(itemRequest.getId());
        itemRequestDTOOutput.setDescription(itemRequest.getDescription());
        log.debug("Устанавливаем пользователя, который создает запрос на вещь в маппере создания запросов");
        User requestor = new User();
        requestor.setId(userId);
        requestor.setName(userRepository.getById(userId).getName());
        requestor.setEmail(userRepository.getById(userId).getEmail());
        itemRequestDTOOutput.setRequestor(requestor);
        itemRequestDTOOutput.setCreated(itemRequest.getCreated());
        log.debug("В маппере запросов устанавливаем значение вещи (возможно кто-то создал вещь по нашему запросу)");
        ItemDTO itemDTO = serviceItem.getItemDTOByRequestId(itemRequest.getId());
        ShortItemForAnswerOnQuery item = new ShortItemForAnswerOnQuery();
        List<ShortItemForAnswerOnQuery> items = new ArrayList<>();
        if (itemDTO != null) {
            item.setId(itemDTO.getId());
            item.setName(itemDTO.getName());
            item.setDescription(itemDTO.getDescription());
            item.setAvailable(itemDTO.getAvailable());
            item.setOwnerId(itemDTO.getOwner().getId());
            item.setRequestId(itemDTO.getRequestId());
            items.add(item);
            itemRequestDTOOutput.setItems(items);
        } else {
            itemRequestDTOOutput.setItems(new ArrayList<>());
        }
        log.debug("В маппере запросов все прошло хорошо");
        return itemRequestDTOOutput;
    }
}
