package ru.practicum.shareit.booking.service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.commons.exceptions.IncorrectDataException;
import ru.practicum.shareit.commons.exceptions.NotFoundException;
import ru.practicum.shareit.commons.exceptions.UserIsNotSharerException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@RequiredArgsConstructor
@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

    private static final Sort SORT_START_DESC = Sort.by(Direction.DESC, "startDate");

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    public Collection<BookingFullDto> findAllByBookerAndState(Long bookerId, BookingState bookingState, Integer from, Integer size) {
        log.debug("Запрос бронирований, созданных пользователем на уровне сервиса");

        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + bookerId + " не найден"));
        log.debug("Передан идентификатор бронирующего: {}", booker.getEntityId());

        log.debug("Передано состояние бронирования: {}", bookingState);

        PageRequest pageRequest = PageRequest.of(from, size, SORT_START_DESC);
        log.debug("Сформированы ограничения коллекции и порядок сортировки");

        Collection<Booking> searchResult = switch (bookingState) {
            case ALL -> bookingRepository.findAllByBookerEntityId(booker.getEntityId(), pageRequest).getContent();
            case CURRENT -> bookingRepository.findAllCurrentBookerBookings(booker.getEntityId(),
                    LocalDateTime.now(), BookingStatus.APPROVED, pageRequest).getContent();
            case FUTURE -> bookingRepository.findAllFutureBookerBookings(booker.getEntityId(), LocalDateTime.now(),
                    BookingStatus.APPROVED, pageRequest).getContent();
            case PAST -> bookingRepository.findAllPastBookerBookings(booker.getEntityId(), LocalDateTime.now(),
                    BookingStatus.APPROVED, pageRequest).getContent();
            case REJECTED ->
                    bookingRepository.findAllBookerBookingsByStatus(booker.getEntityId(), BookingStatus.REJECTED,
                            pageRequest).getContent();
            case WAITING -> bookingRepository.findAllBookerBookingsByStatus(booker.getEntityId(), BookingStatus.WAITING,
                    pageRequest).getContent();
        };
        log.debug("На уровень сервиса вернулась коллекция бронирований пользователя размером {}", searchResult.size());

        Collection<BookingFullDto> result = completeCollection(searchResult);
        log.debug("Коллекция бронирования преобразована");

        log.debug("Возврат результатов запроса на уровень контроллера");
        return result;
    }

    @Override
    public Collection<BookingFullDto> findAllByOwnerAndState(Long ownerId, String state, Integer from, Integer size) {
        log.debug("Запрос бронирований на вещи владельца на уровне сервиса");

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + ownerId + " не найден"));
        log.debug("Передан идентификатор владельца: {}", owner.getEntityId());

        BookingState bookingState = BookingState.of(state);
        log.debug("Передано состояние бронирований: {}", bookingState);

        PageRequest pageRequest = PageRequest.of(from, size, SORT_START_DESC);

        Collection<Booking> searchResult = switch (bookingState) {
            case ALL -> bookingRepository.findAllByItemSharerEntityId(owner.getEntityId(),
                    pageRequest).getContent();
            case CURRENT -> bookingRepository.findAllCurrentOwnerBookings(owner.getEntityId(), LocalDateTime.now(),
                    BookingStatus.APPROVED, pageRequest).getContent();
            case FUTURE -> bookingRepository.findAllFutureOwnerBookings(owner.getEntityId(), LocalDateTime.now(),
                    BookingStatus.APPROVED, pageRequest).getContent();
            case PAST -> bookingRepository.findAllPastOwnerBookings(owner.getEntityId(), LocalDateTime.now(),
                    BookingStatus.APPROVED, pageRequest).getContent();
            case REJECTED -> bookingRepository.findAllOwnerBookingsByStatus(owner.getEntityId(), BookingStatus.REJECTED,
                    pageRequest).getContent();
            case WAITING -> bookingRepository.findAllOwnerBookingsByStatus(owner.getEntityId(), BookingStatus.WAITING,
                    pageRequest).getContent();
        };
        log.debug("На уровень сервиса вернулась коллекция бронирования вещей владельца размером {}",
                searchResult.size());

        Collection<BookingFullDto> result = completeCollection(searchResult);
        log.debug("Коллекция бронирования вещей владельца преобразована");

        log.debug("Возврат результатов поиска бронирования вещей владельца на уровень контроллера");
        return result;
    }

    @Override
    public BookingFullDto findByBookerIdAndBookingId(Long bookerId, Long bookingId) {
        log.debug("Запрос бронирования по идентификатору на уровне сервиса");

        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + bookerId + " не найден"));
        log.debug("Передан идентификатор пользователя, инициировавшего запрос: {}", booker.getEntityId());

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));
        log.debug("Передан идентификатор бронирования: {}", booking.getEntityId());

        log.debug("Проверка: является ли пользователь с id {} инициатором бронирования с id {}", booker.getEntityId(),
                booking.getEntityId());
        boolean isBookingBooker = booking.getBooker().getEntityId().equals(booker.getEntityId());

        if (!isBookingBooker) {
            log.debug("Пользователь не является инициатором бронирования.");

            log.debug("Проверка: является ли пользователь с id {} владельцем бронируемой вещи", booker.getEntityId());
            boolean isBookedItemOwner = bookingRepository.existsByBookingAndOwner(booking.getEntityId(),
                    booker.getEntityId());

            if (!isBookedItemOwner) {
                throw new UserIsNotSharerException("Пользователь с id " + booker.getEntityId()
                        + " не является автором бронирования или владельцем бронируемой вещи");
            } else {
                log.debug("Пользователь является владельцем вещи");
            }
        } else {
            log.debug("Пользователь является инициатором бронирования");
        }
        log.debug("Проверки завершены");

        // Преобразуем модель
        BookingFullDto result = bookingMapper.mapToFullDto(booking);
        result.setItem(itemMapper.mapToShortDto(booking.getItem()));
        result.getItem().setSharer(userMapper.mapToUserDto(booking.getItem().getSharer()));
        result.setBooker(userMapper.mapToUserDto(booking.getBooker()));
        log.debug("Полученная модель преобразована");

        log.debug("Возврат результата поиска на уровень контроллера");
        return result;
    }

    @Override
    @Transactional
    public BookingFullDto create(Long bookerId, BookingCreateDto dto) {
        log.debug("Создание бронирования на уровне сервиса");

        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + bookerId + " не найден"));
        log.debug("Передан идентификатор инициатора бронирования: {}", booker.getEntityId());

        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Бронируемая вещь с id " + dto.getItemId() + " не найдена"));
        if (!item.getAvailable()) {
            throw new IncorrectDataException("Вещь с id " + item.getEntityId() + " не доступна для бронирования");
        }
        log.debug("Получен идентификатор бронируемой вещи: {}", item.getEntityId());

        Booking booking = bookingMapper.mapToBooking(dto);
        booking.setItem(item);
        booking.setBooker(booker);
        log.debug("Сохраняемая модель преобразована");

        bookingRepository.save(booking);
        log.debug("На уровень сервиса после сохранения вернулось бронирование с id {}", booking.getEntityId());

        BookingFullDto result = bookingMapper.mapToFullDto(booking);
        result.setItem(itemMapper.mapToShortDto(item));
        result.getItem().setSharer(userMapper.mapToUserDto(item.getSharer()));
        result.setBooker(userMapper.mapToUserDto(booker));
        log.debug("Полученная после сохранения модель преобразована");

        log.debug("Возврат результатов сохранения на уровень контроллера");
        return result;
    }

    @Override
    @Transactional
    public BookingFullDto approve(Long ownerId, Long bookingId, Boolean approved) {
        log.debug("Изменение согласования бронирования на уровне сервиса");

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));
        log.debug("Передан идентификатор согласуемого бронирования: {}", booking.getEntityId());
        log.debug("Передан идентификатор владельца вещи: {}", ownerId);
        log.debug("Передан статус согласования бронирования: {}", approved);

        boolean isItemOwner = booking.getItem().getSharer().getEntityId().equals(ownerId);

        if (!isItemOwner) {
            throw new IncorrectDataException(
                    "Пользователь с id " + ownerId + " не является владельцем бронируемой вещи");
        }
        log.debug("Пользователь с id {} является владельцем бронируемой вещи", ownerId);

        // Устанавливаем статус бронирования
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        bookingRepository.save(booking);
        log.debug("Изменения бронирования сохранены");

        BookingFullDto result = bookingMapper.mapToFullDto(booking);
        result.setItem(itemMapper.mapToShortDto(booking.getItem()));
        result.getItem().setSharer(userMapper.mapToUserDto(booking.getItem().getSharer()));
        result.setBooker(userMapper.mapToUserDto(booking.getBooker()));
        log.debug("Измененная модель преобразована");

        log.debug("Возврат результатов согласования бронирования на уровень контроллера");
        return result;
    }

    /**
     * Метод преобразует коллекцию бронирований
     *
     * @param searchResult коллекция-источник
     * @return преобразованная коллекция {@link BookingFullDto}
     */
    private Collection<BookingFullDto> completeCollection(Collection<Booking> searchResult) {
        // Преобразуем коллекцию
        Collection<BookingFullDto> result = searchResult.stream()
                .map(bookingMapper::mapToFullDto)
                .toList();

        // Дополним коллекцию бронируемыми вещами и инициаторами бронирований
        for (BookingFullDto dto : result) {
            // Найдем бронируемую вещь
            Optional<Item> item = searchResult.stream()
                    .filter(book -> book.getEntityId().equals(dto.getId()))
                    .map(Booking::getItem)
                    .findFirst();
            item.ifPresent(value -> {
                // Установим вещь
                dto.setItem(itemMapper.mapToShortDto(value));
                // Установим владельца вещи
                dto.getItem().setSharer(userMapper.mapToUserDto(item.get().getSharer()));
            });

            // Найдём инициатора бронирования
            Optional<User> booker = searchResult.stream()
                    .filter(book -> book.getEntityId().equals(dto.getId()))
                    .map(Booking::getBooker)
                    .findFirst();
            // Установим его
            booker.ifPresent(value -> dto.setBooker(userMapper.mapToUserDto(value)));
        }
        return result;
    }
}
