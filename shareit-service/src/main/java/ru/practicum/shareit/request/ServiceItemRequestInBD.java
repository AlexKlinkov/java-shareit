package ru.practicum.shareit.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.errorHandlerException.NotFoundException;
import ru.practicum.shareit.errorHandlerException.ValidationException;
import ru.practicum.shareit.item.ServiceItemInDB;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Component("ServiceItemRequestInBD")
@Data
@NoArgsConstructor
public class ServiceItemRequestInBD implements ServiceItemRequest {

    @Qualifier("ServiceItemInDB")
    @Autowired
    private ServiceItemInDB serviceItem;
    @Autowired
    private ItemRequestMapperInitialization itemRequestMapper =
            new ItemRequestMapperInitialization(getServiceItem());


    @Override
    public ItemRequestDTOOutput create(int userId, ItemRequestDTOInput itemRequestDTOInput) {
        log.debug("При создании запроса на вещь, проверяем, что такой пользователь существует");
        if (!serviceItem.getUserRepository().existsById(userId)) {
            throw new NotFoundException("При создании запроса на вещь, данный пользователь не был найден");
        }
        log.debug("Получаем объект БД при создании запроса на вещь");
        ItemRequest itemRequest = itemRequestMapper.itemRequestFromItemRequestDTOInput(itemRequestDTOInput, userId);
        log.debug("Устанавливаем время создания запроса на вещь");
        itemRequest.setCreated(LocalDateTime.now());
        log.debug("Возвращаем запрос пользователя на вещь");
        return itemRequestMapper.itemRequestDTOOutPutFromItemRequest(
                serviceItem.getItemRequestRepository().save(itemRequest), userId);
    }

    @Override
    public List<ItemRequestDTOOutput> getItemRequestsOfUser(int userId, Integer from, Integer size, String sort) {
        log.debug("Проверяем, что пользователь с таким ID существует в БД при попытке получить результат " +
                "запросов на вещи по странично");
        if (!serviceItem.getUserRepository().existsById(userId)) {
            throw new NotFoundException("Пользователя с таким ID в БД нет, " +
                    "не получится получить страницы с запросами на вещи");
        }
        log.debug("Проверяем, что старт и финиш (с какую по какую страницу) не равны друг другу");
        if (from == size) {
            throw new ValidationException("Ошибка валидации, выбрано ноль страниц");
        }
        log.debug("Проверяем, что страница старта не отрицательная и количества страниц тоже");
        if ((from < 0 || size < 0) || (from < 0 && size < 0)) {
            throw new ValidationException("Ошибка валидации, выбрана отрицательная начальная страница или " +
                    "Отрицательное количество страниц или и то и другое вместе");
        }
        List<ItemRequestDTOOutput> itemRequestDTOOutput = new ArrayList<>();
        Page<ItemRequest> page = serviceItem.getItemRequestRepository().findAllByRequestorIdNot(userId, PageRequest.of(
                from, size, Sort.by(sort)
        ));
        log.debug("Есть что вернуть на страницах запроса конкретного пользователя");
        itemRequestDTOOutput.addAll(page.stream()
                .map(itemRequest -> itemRequestMapper.itemRequestDTOOutPutFromItemRequest(itemRequest, userId))
                .collect(toList()));
        return itemRequestDTOOutput;
    }

    @Override
    public ItemRequestDTOOutput getRequestByRequestIdAndUserId(int userId, int itemRequestId) {
        log.debug("При попытке получить конкретный запрос на вещь конкретного пользователя проверяем, " +
                "что такой пользователь существует");
        if (!serviceItem.getUserRepository().existsById(userId)) {
            throw new NotFoundException("При получении запроса на вещь, конкретного пользователя, " +
                    "данный пользователь не был найден");
        }
        log.debug("При попытке получить конкретный запрос на вещь конкретного пользователя проверяем, " +
                "что такой запрос существует");
        if (!serviceItem.getItemRequestRepository().existsById(itemRequestId)) {
            throw new NotFoundException("При получении запроса на вещь, конкретного пользователя, " +
                    "данный запрос не был найден в БД");
        }
        ItemRequest itemRequest = serviceItem.getItemRequestRepository().findAllByRequestorIdAndId(userId,
                itemRequestId);
        log.debug("При попытке получить конкретный запрос проверяем, что он есть в БД, иначе создаем новый");
        if (itemRequest == null) {
            log.debug("При попытке получить конкретный запрос по пользователю и ID, " +
                    "возвращаем только данные по запросу");
            ItemRequest itemRequest1 = serviceItem.getItemRequestRepository().getById(itemRequestId);
            ItemRequestDTOOutput itemRequestDTOOutputForReturn = itemRequestMapper.itemRequestDTOOutPutFromItemRequest(
                    itemRequest1, userId
            );

            return itemRequestDTOOutputForReturn;
        }
        return itemRequestMapper.itemRequestDTOOutPutFromItemRequest(itemRequest, userId);
    }

    @Override
    public List<ItemRequestDTOOutput> getItemRequestsByUserId(int userId) {
        log.debug("Проверяем существование пользователя при попытке получить все запросы на вещи по id пользователя " +
                "(не постранично)");
        if (!serviceItem.getUserRepository().existsById(userId)) {
            throw new NotFoundException("При получении запроса на список вещей, конкретного пользователя, " +
                    "данный пользователь не был найден");
        }
        log.debug("Получаем список всех запросов пользователя в методе по возвращению всех запросов пользователя " +
                "не по странично !!");
        List<ItemRequest> ListFromBD = new ArrayList<>(
                serviceItem.getItemRequestRepository().findAllByRequestor_Id(userId));
        List<ItemRequestDTOOutput> returnList = new ArrayList<>();
        if (!ListFromBD.isEmpty()) {
            for (ItemRequest itemRequest : ListFromBD) {
                ItemRequestDTOOutput itemRequestDTOOutputWithExtraFields =
                        itemRequestMapper.itemRequestDTOOutPutFromItemRequest(itemRequest, userId);
                returnList.add(itemRequestDTOOutputWithExtraFields);
            }
            returnList.sort(Comparator.comparing(ItemRequestDTOOutput::getCreated));
        }
        return returnList;
    }
}