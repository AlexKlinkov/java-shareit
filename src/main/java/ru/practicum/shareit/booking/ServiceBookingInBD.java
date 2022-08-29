package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestDTOOutput;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@Component("ServiceBookingInBD")
public class ServiceBookingInBD implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

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

/*    public Page<Booking> getBookingsByOwnerId(int userId, String state, Pageable pageable, String key) {
        log.debug("Проверяем поле статус");
        if (!state.equals("CURRENT") && !state.equals("ALL") && !state.equals("PAST") && !state.equals("FUTURE") &&
                !state.equals("WAITING") && !state.equals("REJECTED")) {
            throw new MyMethodArgumentTypeMismatchException("state", state);
        }
        log.debug("Проверяем, что пользователь по переданному ID есть в БД (метод получения бронирования по ID)");
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователя с таким ID нет, не получилось получить информацию по бронированию");
        }
        log.debug("Получаем страницы со всеми бронированиями отсортированными от новых к старым по дате старта");
        Page<Booking> pageReturn;
        if (state.equals("ALL") && key.equals("booker")) {
            pageReturn = bookingRepository.findAllByItemOwnerId(userId, pageable);
            return pageReturn;
        }
        LocalDateTime now = LocalDateTime.now(); // Текущее время
        log.debug("Получаем страницы со всеми будующими бронированиями");
        if (state.equals("FUTURE")) {
            pageReturn = bookingRepository.findAllByItemOwnerIdAndStartIsAfter(userId, now, pageable);
            return pageReturn;
        }
        log.debug("Получение списка текущих бронирований текущего пользователя");
        if (state.equals("CURRENT")) {
            pageReturn = bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(userId,
                            now, now, pageable);
            return pageReturn;
        }
        log.debug("Получение списка завершенных бронирований текущего пользователя");
        if (state.equals("PAST")) {
            pageReturn = bookingRepository.findAllByItemOwnerIdAndEndIsBefore(userId, now, pageable);
            return pageReturn;
        }
        log.debug("Получение списка бронирований с отклоненным статусом бронирования для текущего пользователя");
        if (state.equals("REJECTED") && key.equals("owner")) {
            pageReturn = bookingRepository.findAllByItemOwnerIdAndStatus(userId, TypeOfStatus.REJECTED, pageable);
            return pageReturn;
        }
        return null;
    }

    public Page<Booking> getBookingsByBookerId(int userId, String state, Pageable pageable, String key) {
        log.debug("Проверяем  статус");
        if (!state.equals("PAST") && !state.equals("ALL") &&!state.equals("CURRENT") && !state.equals("FUTURE") &&
                !state.equals("WAITING") && !state.equals("REJECTED")) {
            throw new MyMethodArgumentTypeMismatchException("state", state);
        }
        log.debug("Проверяем, что пользователь по переданному ID есть в БД (метод получения бронирования по ID)");
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователя с таким ID нет, не получилось получить информацию по бронированию");
        }
        log.debug("Получаем страницы со всеми бронированиями отсортированными от (новых к старым) по дате старта");
        Page<Booking> pageReturn;
        if (state.equals("ALL") && key.equals("booker")) {
            pageReturn = bookingRepository.findAllByBookerId(userId, pageable);
            return pageReturn;
        }
        LocalDateTime now = LocalDateTime.now(); // Текущее время
        log.debug("Получаем страницы со всеми будующими бронированиями");
        if (state.equals("FUTURE")) {
            pageReturn = bookingRepository.findAllByBookerIdAndStartIsAfter(userId, now, pageable);
            return pageReturn;
        }
        log.debug("Получение списка текущих бронирований текущего пользователя");
        if (state.equals("CURRENT")) {
            pageReturn = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(userId,
                    now, now, pageable);
            return pageReturn;
        }
        log.debug("Получение списка завершенных бронирований текущего пользователя");
        if (state.equals("PAST")) {
            pageReturn = bookingRepository.findAllByBookerIdAndEndIsBefore(userId, now, pageable);
            return pageReturn;
        }
        if (state.equals("REJECTED") && key.equals("booker")) {
            pageReturn = bookingRepository.findAllByBookerIdAndStatus(userId, TypeOfStatus.REJECTED, pageable);
            return pageReturn;
        }
        return null;
    }*/
/*
    @Override
    public List<BookingDTOOutput> getBookingsByBookerId(int userId, String state,
                                                                 Integer from, Integer size) {
        log.debug("Проверяем поле статус  запросе");
        if (!state.equals("ALL") && !state.equals("CURRENT") && !state.equals("PAST") && !state.equals("FUTURE") &&
                !state.equals("WAITING") && !state.equals("REJECTED")) {
            throw new MyMethodArgumentTypeMismatchException("state", state);
        }
        log.debug("Проверяем, что пользователь по переданному ID есть в БД (метод получения бронирования по ID)");
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователя с таким ID нет, не получилось получить информацию по бронированию");
        }
        log.debug("Проверяем, что страница старта не отрицательная и количества страниц тоже");
        if ((from < 0 || size < 0) || (from < 0 && size < 0)) {
            throw new ValidationException("Ошибка валидации, выбрана отрицательная начальная страница или " +
                    "Отрицательное количество страниц или и то и другое вместе");
        }
        log.debug("Проверяем, что старт и финиш  не равны друг другу");
        if (from.equals(size)) {
            throw new ValidationException("Ошибка валидации, выбрано ноль страниц");
        }
        Page<Booking> page = bookingRepository.findAllByBookerId(userId,
                PageRequest.of(from, size, Sort.by(Sort.Direction.DESC)));
        log.debug("Все запросы пользователя");
        List<BookingDTOOutput> bookingDTOOutputListReturn = page.stream()
                .map(bookingMapper::bookingDTOOutputFromBooking)
                .collect(toList());
        log.debug("Получаем список со всеми бронированиями отсортированными от новых к старым по дате старта");
        List<BookingDTOOutput> bookings = new ArrayList<>();
        for (BookingDTOOutput book : bookingDTOOutputListReturn) {
            if (book.getBooker().getId() == userId || book.getItem().getOwner().getId() == userId) {
                bookings.add(book);
            }
        }
        log.debug("Получение списка всех бронирований текущего пользователя");
        LocalDateTime now = LocalDateTime.now(); // Текущее время
        if (state.equals("ALL")) {
            return bookings;
        }
        ArrayList<BookingDTOOutput> returnList = new ArrayList<>();
        log.debug("Получение списка будущих бронирований текущего пользователя");
        if (state.equals("FUTURE")) {
            for (BookingDTOOutput booking : bookings) {
                if (booking.getStart().isAfter(now)) {
                    returnList.add(booking);
                }
            }
            return returnList;
        }
        log.debug("Получение списка текущих бронирований текущего пользователя");
        if (state.equals("CURRENT")) {
            for (BookingDTOOutput booking : bookings) {
                if (now.isAfter(booking.getStart()) && now.isBefore(booking.getEnd())) {
                    returnList.add(booking);
                }
            }
            return returnList;
        }
        log.debug("Получение списка завершенных бронирований текущего пользователя");
        if (state.equals("PAST")) {
            for (BookingDTOOutput booking : bookings) {
                if (now.isAfter(booking.getEnd())) {
                    returnList.add(booking);
                }
            }
            return returnList;
        }
        log.debug("Получение списка бронирований ожидающих подтверждения для текущего пользователя");
        if (state.equals("WAITING")) {
            for (BookingDTOOutput booking : bookings) {
                if (booking.getStatus().equals(TypeOfStatus.WAITING)) {
                    returnList.add(booking);
                }
            }
            return returnList;
        }
        log.debug("Получение списка бронирований с отклоненным статусом бронирования для текущего пользователя");
        if (state.equals("REJECTED")) {
            for (BookingDTOOutput booking : bookings) {
                if (booking.getBooker().getId() == userId && booking.getStatus().equals(TypeOfStatus.REJECTED)) {
                    returnList.add(booking);
                }
            }
        }
        return returnList;
    }*/

    @Override
    public List<BookingDTOOutput> getBookingsByOwnerIdOrBookingID(int userId, String state,
                                                                 int from, int size, String key) {
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
        if ((from < 0 || size < 0) || (from < 0 && size < 0)) {
            throw new ValidationException("Ошибка валидации, выбрана отрицательная начальная страница или " +
                    "Отрицательное количество страниц или и то и другое вместе");
        }
        log.debug("Проверяем, что старт и финиш  не равны друг другу");
        if (from == from + size) {
            System.out.println("Тут");
            throw new ValidationException("Ошибка валидации, выбрано ноль страниц");
        }
        Page<Booking> pageOwner = bookingRepository.findAllByItemOwnerId(userId,
                PageRequest.of(from, size));
        Page<Booking> pageBooker = bookingRepository.findAllByBookerId(userId,
                PageRequest.of(from, size));
        List<Booking> bookingListOwner = pageOwner.stream()
                .map(booking -> bookingRepository.getById(booking.getId()))
                .collect(toList());
        List<Booking> bookingListBooker = pageBooker.stream()
                .map(booking -> bookingRepository.getById(booking.getId()))
                .collect(toList());
        log.debug("Получаем список со всеми бронированиями отсортированными от новых к старым по дате старта");
        List<Booking> allBookings = new ArrayList<>();
        allBookings.addAll(bookingListOwner);
        allBookings.addAll(bookingListBooker);
        List<BookingDTOOutput> bookings = new ArrayList<>();
        for (Booking book : allBookings) {
            if (book.getBooker().getId() == userId ||
                    book.getItem().getOwner().getId() == userId) {
                bookings.add(bookingMapper.bookingDTOOutputFromBooking(book));
            }
        }
        bookings.sort(Comparator.comparing(BookingDTOOutput::getStart).reversed());
        log.debug("Получение списка всех бронирований текущего пользователя");
        LocalDateTime now = LocalDateTime.now(); // Текущее время
        List<BookingDTOOutput> returnList = new ArrayList<>();
        if (state.equals("ALL")) {
            returnList.addAll(bookings);
            return returnList;
        }
        log.debug("Получение списка будущих бронирований текущего пользователя");
        if (state.equals("FUTURE")) {
            for (BookingDTOOutput booking : bookings) {
                if (booking.getStart().isAfter(now)) {
                    returnList.add(booking);
                }
            }
            return returnList;
        }
        log.debug("Получение списка текущих бронирований текущего пользователя");
        if (state.equals("CURRENT")) {
            for (BookingDTOOutput booking : bookings) {
                if (now.isAfter(booking.getStart()) && now.isBefore(booking.getEnd())) {
                    returnList.add(booking);
                }
            }
            return returnList;
        }
        log.debug("Получение списка завершенных бронирований текущего пользователя");
        if (state.equals("PAST")) {
            for (BookingDTOOutput booking : bookings) {
                if (now.isAfter(booking.getEnd())) {
                    returnList.add(booking);
                }
            }
            return returnList;
        }
        log.debug("Получение списка бронирований ожидающих подтверждения для текущего пользователя");
        if (state.equals("WAITING")) {
            for (BookingDTOOutput booking : bookings) {
                if (booking.getStatus().equals(TypeOfStatus.WAITING)) {
                    returnList.add(booking);
                }
            }
            return returnList;
        }

        log.debug("Получение списка бронирований с отклоненным статусом бронирования для текущего пользователя");
        if (state.equals("REJECTED") && key.equals("booker")) {
            for (Booking booking : allBookings) {
                if (booking.getBooker().getId() == userId && booking.getStatus().equals(TypeOfStatus.REJECTED)) {
                    returnList.add(bookingMapper.bookingDTOOutputFromBooking(booking));
                }
            }
        }
        if (state.equals("REJECTED") && key.equals("owner")) {
            for (Booking booking : allBookings) {
                if (booking.getItem().getOwner().getId() == userId &&
                        booking.getStatus().equals(TypeOfStatus.REJECTED)) {
                    returnList.add(bookingMapper.bookingDTOOutputFromBooking(booking));
                }
            }
        }
        return returnList;
    }
}
