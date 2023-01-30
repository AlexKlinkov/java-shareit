package ru.practicum.shareit.booking;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.errorHandlerException.MyMethodArgumentTypeMismatchException;
import ru.practicum.shareit.errorHandlerException.NotFoundException;
import ru.practicum.shareit.errorHandlerException.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@Data
@Component("ServiceBookingInBD")
public class ServiceBookingInBD implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;
    List<BookingDTOOutput> finalReturnList = new ArrayList<>();

    @Autowired
    public ServiceBookingInBD(BookingRepository bookingRepository, UserRepository userRepository,
                              ItemRepository itemRepository,
                              @Qualifier("BookingMapperInitialization") BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingMapper = bookingMapper;
    }

    @Override
    public BookingDTOOutput create(int user_id, BookingDTOInput booking) {
        log.debug("Проверяем есть ли пользователь с таким ID при бронировании вещи");
        if (!userRepository.existsById(user_id)) {
            throw new NotFoundException("Пользователя с таким ID нет (создание брони)");
        }
        log.debug("Проверяем есть ли вещь с таким ID при бронировании");
        if (!itemRepository.existsById(booking.getItemId())) {
            throw new NotFoundException("Вещи с таким ID для бронирования нет (создание вещи)");
        }
        log.debug("Проверяем, что владелец вещи, не может забронировать свою же вещь");
        if (itemRepository.getById(booking.getItemId()).getOwner().getId() == user_id) {
            throw new NotFoundException("Вещь не может быть забронирована ее собственником");
        }
        log.debug("Если в бронировании было отказано, не создаем новое с новым статусом, а обновляем существующее" +
                " при повторной попытке забронировать вещь");
        if (bookingRepository.existsById(booking.getItemId())) {
            Booking bookWhichAlreadyExist = bookingRepository.getById(booking.getItemId());
            if (bookWhichAlreadyExist.getBooker().getId() == user_id) {
                bookWhichAlreadyExist.setStatus(TypeOfStatus.WAITING);
                bookingRepository.save(bookWhichAlreadyExist);
            }
        }
        log.debug("Проверяем доступна ли вещь для бронирования");
        if (!itemRepository.getById(booking.getItemId()).getAvailable()) {
            throw new ValidationException("Данная вещь не доступна сейчас для бронирования");
        }
        log.debug("Сохраняем информацию о бронировании при создании в БД");
        Booking bookingToSaveInBD = bookingMapper.bookingFromBookingDTOInput(booking, user_id);
        bookingRepository.save(bookingToSaveInBD);
        return bookingMapper.bookingDTOOutputFromBooking(bookingRepository.save(bookingToSaveInBD));
    }

    @Override
    public BookingDTOOutput updateStatusOfBooking(Integer ownerId, Integer bookingId, Boolean approved) {
        log.debug("Получаем бронирование из бд при попытке изменить статус подтвержденности");
        Booking booking = bookingRepository.getById(bookingId);
        log.debug("Получаем вещь, которая относится к данному бронированию (обновляем статус подтвержденности)");
        Item item = itemRepository.getById(booking.getItem().getId());
        log.debug("Проверяем, что статус еще не подтвержденный, иначе его уже нельзя поменять у бронирования");
        if (booking.getStatus().equals(TypeOfStatus.APPROVED)) {
            throw new ValidationException("Статус бронирования уже подтвержден, теперь его нельзя поменять");
        }
        log.debug("Проверяем что ID полученное от клиента соответствует ID хозяина вещи " +
                "(обновляем статус подтвержденности)");
        if (!item.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Пользователь с данным ID не может менять подтвержденность не своей вещи");
        }
        log.debug("Меняем статус подтвержденности бронирования вещи");
        if (approved) {
            booking.setStatus(TypeOfStatus.APPROVED);
        }
        if (!approved) {
            booking.setStatus(TypeOfStatus.REJECTED);
        }
        return bookingMapper.bookingDTOOutputFromBooking(bookingRepository.save(booking));
    }

    @Override
    public BookingDTOOutput findBookingByIdAndUserId(int userId, int bookingId) {
        log.debug("Проверяем, что бронирование при возврате пользователю с таким ID существует");
        if (!bookingRepository.existsById(bookingId)) {
            throw new NotFoundException("Бронирование по данному ID не может быть возвращено пользователю, так " +
                    "как такого бронирования не существует");
        }
        log.debug("Проверяем, что бронирование принадлежит этому пользователю или владельцу вещи");
        if (!(bookingRepository.getById(bookingId).getBooker().getId() == userId ||
                bookingRepository.getById(bookingId).getItem().getOwner().getId() == userId)) {
            throw new NotFoundException("Бронирование по данному ID не может быть возвращено пользователю, так " +
                    "как это бронирование к ниму не относится");
        }
        List<Booking> bookings = bookingRepository.findAll();
        for (Booking booking : bookings) {
            if ((booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) &&
                    booking.getId() == bookingId) {
                return bookingMapper.bookingDTOOutputFromBooking(booking);
            }
        }
        return null;
    }

    @Override
    public List<BookingDTOOutput> getBookingsByOwnerIdOrBookingID(int userId, String state,
                                                                  int from, int size, String key) {
        finalReturnList.clear();
        log.debug("Проверяем поле статус  запросе");
        if (!state.equals("ALL") && !state.equals("CURRENT") && !state.equals("PAST") && !state.equals("FUTURE") &&
                !state.equals("WAITING") && !state.equals("REJECTED")) {
            throw new MyMethodArgumentTypeMismatchException("state", state);
        }
        log.debug("Проверяем, что пользователь по переданному ID есть в БД (метод получения бронирования по ID)");
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователя с таким ID нет, не получилось получить информацию по бронированию");
        }

        log.debug("Проверяем, что страница старта не отрицательная/количества страниц тоже");
        if (from < 0 || size < 0) {
            throw new ValidationException("Ошибка валидации, выбрана отрицательная начальная страница или " +
                    "Отрицательное количество страниц или и то и другое вместе");
        }
        log.debug("Проверяем, что старт и финиш  не равны друг другу");
        if (size == 0) {
            throw new ValidationException("Ошибка валидации, выбрано ноль страниц");
        }

        log.debug("Получаем постраничный список с Bookings");
        LocalDateTime now = LocalDateTime.now(); // Текущее время
        log.debug("Устанавливаем индекс для перебора возвращаемых страниц");
        int indexOfPage = 0;
        Pageable page = PageRequest.of(indexOfPage, 20, Sort.by("start").descending());
        int amountOfPages;
        if (key.equals("ALL")) {
            switch (state) {
                case "ALL":
                    amountOfPages = bookingRepository.findAllByBookerId(userId, page).getTotalPages();
                    for (int i = indexOfPage; i <= amountOfPages; i++) {
                        getBookingDTOOutputs(bookingRepository.findAllByBookerId(userId, page), from, size, i);
                        page.next();
                    }
                    break;
                case "FUTURE":
                    amountOfPages = bookingRepository.findAllByBookerIdAndStartIsAfter(userId, now, page).getTotalPages();
                    for (int i = indexOfPage; i <= amountOfPages; i++) {
                        getBookingDTOOutputs(bookingRepository.findAllByBookerIdAndStartIsAfter(userId, now, page),
                                from, size, i);
                        page.next();
                    }
                    break;
                case "CURRENT":
                    amountOfPages = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(userId, now,
                            now, page).getTotalPages();
                    for (int i = indexOfPage; i <= amountOfPages; i++) {
                        getBookingDTOOutputs(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(userId, now,
                                now, page), from, size, i);
                        page.next();
                    }
                    break;

                case "PAST":
                    amountOfPages = bookingRepository.findAllByBookerIdAndEndIsBefore(userId, now, page).getTotalPages();
                    for (int i = indexOfPage; i <= amountOfPages; i++) {
                        getBookingDTOOutputs(bookingRepository.findAllByBookerIdAndEndIsBefore(userId, now, page),
                                from, size, i);
                        page.next();
                    }
                    break;

                case "WAITING":
                    amountOfPages = bookingRepository.findAllByBookerIdAndStatus(userId, TypeOfStatus.WAITING,
                            page).getTotalPages();
                    for (int i = indexOfPage; i <= amountOfPages; i++) {
                        getBookingDTOOutputs(bookingRepository.findAllByBookerIdAndStatus(userId, TypeOfStatus.WAITING,
                                page), from, size, i);
                        page.next();
                    }
                    break;

                case "REJECTED":
                    amountOfPages = bookingRepository.findAllByBookerIdAndStatus(userId, TypeOfStatus.REJECTED,
                            page).getTotalPages();
                    for (int i = indexOfPage; i <= amountOfPages; i++) {
                        getBookingDTOOutputs(bookingRepository.findAllByBookerIdAndStatus(userId,
                                TypeOfStatus.REJECTED, page), from, size, i);
                        page.next();
                    }
                    break;
            }
        } else {
            switch (state) {
                case "ALL":
                    amountOfPages = bookingRepository.findAllByItemOwnerId(userId, page).getTotalPages();
                    for (int i = indexOfPage; i <= amountOfPages; i++) {
                        getBookingDTOOutputs(bookingRepository.findAllByItemOwnerId(userId, page),
                                from, size, i);
                        page.next();
                    }
                    break;
                case "FUTURE":
                    amountOfPages = bookingRepository.findAllByItemOwnerIdAndStartIsAfter(userId, now, page).getTotalPages();
                    for (int i = indexOfPage; i <= amountOfPages; i++) {
                        getBookingDTOOutputs(bookingRepository.findAllByItemOwnerIdAndStartIsAfter(userId, now, page),
                                from, size, i);
                        page.next();
                    }
                    break;
                case "CURRENT":
                    amountOfPages = bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(userId, now, now,
                            page).getTotalPages();
                    for (int i = indexOfPage; i <= amountOfPages; i++) {
                        getBookingDTOOutputs(bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(userId,
                                now, now, page), from, size, i);
                        page.next();
                    }
                    break;
                case "PAST":
                    amountOfPages = bookingRepository.findAllByItemOwnerIdAndEndIsBefore(userId, LocalDateTime.now(),
                            page).getTotalPages();
                    for (int i = indexOfPage; i <= amountOfPages; i++) {
                        getBookingDTOOutputs(bookingRepository.findAllByItemOwnerIdAndEndIsBefore(userId,
                                LocalDateTime.now(), page), from, size, i);
                        page.next();
                    }
                    break;
                case "WAITING":
                    amountOfPages = bookingRepository.findAllByItemOwnerIdAndStatus(userId, TypeOfStatus.WAITING,
                            page).getTotalPages();
                    for (int i = indexOfPage; i <= amountOfPages; i++) {
                        getBookingDTOOutputs(bookingRepository.findAllByItemOwnerIdAndStatus(userId, TypeOfStatus.WAITING,
                                page), from, size, i);
                        page.next();
                    }
                    break;
                case "REJECTED":
                    amountOfPages = bookingRepository.findAllByItemOwnerIdAndStatus(userId, TypeOfStatus.REJECTED,
                            page).getTotalPages();
                    for (int i = indexOfPage; i <= amountOfPages; i++) {
                        getBookingDTOOutputs(bookingRepository.findAllByItemOwnerIdAndStatus(userId, TypeOfStatus.REJECTED,
                                page), from, size, i);
                    }
                    break;
            }
        }
        return new ArrayList<>(finalReturnList);
    }

    public void getBookingDTOOutputs(Page<Booking> page, int from, int size, int iterations) {
        List<BookingDTOOutput> bookingList = new ArrayList<>();
        if (from < size && iterations == 0) {
            bookingList = page.stream()
                    .map(bookingMapper::bookingDTOOutputFromBooking)
                    .skip(from)
                    .collect(toList());
        }
        if (from > size && iterations < (((double) from / size) + 1)) {
            if (from % size == 0 && iterations <= (from / size)) {
                bookingList = page.stream()
                        .skip(from)
                        .map(bookingMapper::bookingDTOOutputFromBooking)
                        .collect(toList());
            }
            if (from % size != 0 && iterations <= (from / size)) {
                if (iterations < (from / size)) {
                    bookingList = page.stream()
                            .skip(from)
                            .map(bookingMapper::bookingDTOOutputFromBooking)
                            .collect(toList());
                } else {
                    bookingList = page.stream()
                            .skip(from - size)
                            .map(bookingMapper::bookingDTOOutputFromBooking)
                            .collect(toList());
                }
            }
        } else {
            if (from == size) {
                bookingList = page.stream()
                        .map(bookingMapper::bookingDTOOutputFromBooking)
                        .skip(from)
                        .collect(toList());
            }
        }

        for (BookingDTOOutput bookingDTOOutput : bookingList) {
            if (!finalReturnList.contains(bookingDTOOutput))
                finalReturnList.add(bookingDTOOutput);
        }
    }
}