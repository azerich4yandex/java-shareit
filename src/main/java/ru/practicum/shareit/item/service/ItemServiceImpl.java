package ru.practicum.shareit.item.service;

import jakarta.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.commons.exceptions.NotFoundException;
import ru.practicum.shareit.commons.exceptions.UserIsNotSharerException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@RequiredArgsConstructor
@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    private static final Sort SORT_ITEM_ID_ASC = Sort.by(Direction.ASC, "entityId");
    private static final Sort SORT_COMMENT_CREATED_ASC = Sort.by(Direction.ASC, "created");
    private static final Sort SORT_BOOKING_END_DESC = Sort.by(Direction.DESC, "endDate");

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public Collection<ItemFullDto> findAllByOwner(Long userId) {
        log.debug("Запрос всех вещей на уровне сервиса");

        if (userId == null) {
            throw new ValidationException("Атрибут \"X-Sharer-User-Id\" не найден в заголовке");
        }
        log.debug("Запрос от пользователя с id: {}", userId);

        Collection<Item> searchResult = itemRepository.findAllBySharerEntityId(userId, SORT_ITEM_ID_ASC);
        log.debug("Из репозитория получена коллекция размером {}", searchResult.size());

        Collection<ItemFullDto> result = completeCollection(searchResult);

        log.debug("Полученная коллекция преобразована. Размер полученной коллекции: {}", result.size());

        log.debug("Возврат результатов поиска на уровень контроллера");
        return result;
    }

    @Override
    public Collection<ItemShortDto> findByText(String text) {
        log.debug("Поиск вещей по вхождению подстроки на уровне сервиса");

        if (text == null || text.trim().isBlank()) {
            log.debug("Передано пустое значение подстроки. Возвращаем пустую коллекцию на уровень контроллера");
            return new ArrayList<>();
        }
        log.debug("Передана подстрока: {}", text);

        Collection<Item> searchResult = itemRepository.findAllByNameAndAvailable(text, true, SORT_ITEM_ID_ASC);
        log.debug("На уровне сервиса получен результат поиска по подстроке размером {}", searchResult.size());

        Collection<ItemShortDto> result = searchResult.stream()
                .map(itemMapper::mapToShortDto)
                .toList();
        log.debug("Найденная коллекция преобразована. Размер полученной коллекции {}", result.size());

        log.debug("Возврат результатов поиска по подстроке на уровень контроллера");
        return result;
    }

    @Override
    public ItemFullDto findById(Long itemId) {
        log.debug("Поиск вещи по идентификатору на уровне сервиса");

        if (itemId == null) {
            throw new ValidationException("Id вещи должен быть указан");
        }
        log.debug("Передан id вещи: {}", itemId);

        Item searchResult = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
        log.debug("На уровне хранилища найден пользователь с id {}", searchResult.getEntityId());

        ItemFullDto result = itemMapper.mapToFullDto(searchResult);
        result.setSharer(userMapper.mapToUserDto(searchResult.getSharer()));
        Collection<Comment> comments = commentRepository.findAllByItemEntityId(result.getId(),
                SORT_COMMENT_CREATED_ASC);
        result.setComments(comments.stream().map(commentMapper::mapToShortDto).toList());
        log.debug("Полученная вещь преобразована");

        log.debug("Возврат результатов поиска по id на уровень контроллера");
        return result;
    }

    @Override
    @Transactional
    public ItemShortDto create(Long userId, ItemCreateDto dto) {
        log.debug("Создание вещи на уровне сервиса");

        if (userId == null) {
            throw new ValidationException("Атрибут \"X-Sharer-User-Id\" не найден в заголовке");
        }
        log.debug("Запрос на создание от имени пользователя с id: {}", userId);

        Item item = itemMapper.mapToItem(dto);
        log.debug("Полученная модель преобразована");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        item.setSharer(user);
        log.debug("Владелец создаваемой вещи найден и установлен");

        item = itemRepository.save(item);
        log.debug("Новая вещь сохранена в хранилище");

        ItemShortDto result = itemMapper.mapToShortDto(item);
        log.debug("Сохраненная модель преобразована");

        log.debug("Возврат результатов сохранения на уровень контроллера");
        return result;
    }

    @Override
    @Transactional
    public CommentShortDto createComment(Long itemId, Long authorId, CommentCreateDto dto) {
        log.debug("Создания комментария на уровне сервиса");

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
        log.debug("Передан идентификатор комментируемой вещи: {}", item.getEntityId());

        if (authorId == null) {
            throw new ValidationException("Атрибут \"X-Sharer-User-Id\" не найден в заголовке");
        }
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + authorId + " не найден"));
        log.debug("Передан идентификатор автора комментария: {}", author.getEntityId());

        boolean isBooker = bookingRepository.existsByItemAndBooker(item.getEntityId(), author.getEntityId(),
                LocalDateTime.now(), BookingStatus.APPROVED);

        if (!isBooker) {
            throw new ValidationException(
                    "Пользователь с id " + authorId + " ранее не бронировал комментируемую вещь с id "
                            + item.getEntityId());
        }

        Comment comment = commentMapper.mapToComment(dto);
        comment.setItem(item);
        comment.setAuthor(author);
        log.debug("Сохраняемая модель преобразована");

        commentRepository.save(comment);
        log.debug("Новый комментарий сохранен в хранилище");

        CommentShortDto result = commentMapper.mapToShortDto(comment);
        log.debug("Сохраненная модель комментария преобразована");

        log.debug("Возврат результатов создания комментария на уровень контроллера");
        return result;
    }

    @Override
    @Transactional
    public ItemShortDto update(Long userId, Long itemId, ItemUpdateDto dto) {
        log.debug("Обновление вещи на уровне сервиса");

        if (userId == null) {
            throw new ValidationException("Атрибут \"X-Sharer-User-Id\" не найден в заголовке");
        }
        log.debug("Передан идентификатор пользователя: {}", userId);

        if (itemId == null) {
            throw new ValidationException("Id вещи должен быть указан");
        }
        log.debug("Передан идентификатор обновляемой вещи: {}", itemId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + dto.getItemId() + " не найдена"));

        if (!item.getSharer().getEntityId().equals(userId)) {
            throw new UserIsNotSharerException(
                    "Пользователь с id " + userId + " не является владельцем вещи с id " + item.getEntityId());
        }
        log.debug("В хранилище найдена вещь для обновления с id {}", item.getEntityId());

        dto.setItemId(itemId);
        itemMapper.updateItemFields(dto, item);
        log.debug("Измененная и полученная модели преобразованы");

        item = itemRepository.save(item);
        log.debug("Измененная модель сохранения в хранилище");

        ItemShortDto result = itemMapper.mapToShortDto(item);
        log.debug("Измененная модель преобразована после сохранения изменений");

        log.debug("Возврат результатов изменения на уровень контроллера");
        return result;
    }

    @Override
    @Transactional
    public void delete(Long userId, Long itemId) {
        log.debug("Удаление вещи по идентификатору на уровне сервиса");

        if (userId == null) {
            throw new ValidationException("Атрибут \"X-Sharer-User-Id\" не найден в заголовке");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        log.debug("Запрос на удаление от пользователя с id: {}", user.getEntityId());

        if (itemId == null) {
            throw new ValidationException("Id вещи должен быть указан");
        }
        log.debug("Передан идентификатор вещи: {}", itemId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
        log.debug("Вещь с id {} для удаления найдена в хранилище", item.getEntityId());

        if (!item.getSharer().equals(user)) {
            throw new ValidationException("Пользователь не является владельцем вещи");
        }

        itemRepository.deleteById(item.getEntityId());
        log.debug("На уровень сервиса вернулась информация об успешном удалении вещи из хранилища");

        log.debug("Возврат результатов удаления на уровень контроллера");
    }

    /**
     * Метод преобразует коллекцию вещей
     *
     * @param searchResult коллекция-источник
     * @return преобразованная коллекция {@link ItemFullDto}
     */
    private Collection<ItemFullDto> completeCollection(Collection<Item> searchResult) {
        // Преобразуем коллекцию
        Collection<ItemFullDto> result = searchResult.stream()
                .map(itemMapper::mapToFullDto)
                .toList();

        // Дополним коллекцию датами последнего и следующего бронирований
        for (ItemFullDto item : result) {
            // Найдем последнее бронирование
            Optional<Booking> booking = bookingRepository.findFirstBookingByItemEntityIdAndEndDateIsBeforeAndStatus(
                    item.getId(), LocalDateTime.now(),
                    BookingStatus.APPROVED, SORT_BOOKING_END_DESC);

            // Установим его
            booking.ifPresent(value -> item.setLastBooking(bookingMapper.mapToShortDto(value)));

            // Найдем следующее бронирование
            booking = bookingRepository.findFirstBookingByItemEntityIdAndEndDateIsAfterAndStatus(item.getId(),
                    LocalDateTime.now(), BookingStatus.APPROVED, SORT_BOOKING_END_DESC);

            // Установим его
            booking.ifPresent(value -> item.setNextBooking(bookingMapper.mapToShortDto(value)));

            // Найдем все комментарии
            Collection<Comment> comments = commentRepository.findAllByItemEntityId(item.getId(),
                    SORT_COMMENT_CREATED_ASC);

            // Установим их
            item.setComments(comments.stream().map(commentMapper::mapToShortDto).toList());
        }

        return result;
    }
}
