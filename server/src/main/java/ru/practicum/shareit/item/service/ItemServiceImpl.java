package ru.practicum.shareit.item.service;

import jakarta.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
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
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
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

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    public Collection<ItemFullDto> findAllByOwner(Long userId, Integer from, Integer size) {
        log.debug("Запрос всех вещей на уровне сервиса");
        log.debug("Запрос от пользователя с id: {}", userId);

        PageRequest pageRequest = PageRequest.of(from, size, SORT_ITEM_ID_ASC);
        Collection<Item> searchResult = itemRepository.findAllBySharerEntityId(userId, pageRequest).getContent();
        log.debug("Из репозитория получена коллекция размером {}", searchResult.size());

        Collection<ItemFullDto> result = completeCollection(searchResult);

        log.debug("Полученная коллекция преобразована. Размер полученной коллекции: {}", result.size());

        log.debug("Возврат результатов поиска на уровень контроллера");
        return result;
    }

    @Override
    public Collection<ItemShortDto> findByText(String text, Integer from, Integer size) {
        log.debug("Поиск вещей по вхождению подстроки на уровне сервиса");
        log.debug("Передана подстрока: {}", text);

        PageRequest pageRequest = PageRequest.of(from, size, SORT_ITEM_ID_ASC);

        Collection<Item> searchResult = itemRepository.findAllByNameAndAvailable(text, true, pageRequest).getContent();
        log.debug("На уровне сервиса получен результат поиска по подстроке размером {}", searchResult.size());

        Collection<ItemShortDto> result = searchResult.stream()
                .map(itemMapper::mapToShortDto)
                .toList();
        for (ItemShortDto item : result) {
            // Найдём и установим владельца вещи
            Optional<UserDto> sharer = searchResult.stream()
                    .filter(i -> i.getEntityId().equals(item.getId()))
                    .map(Item::getSharer)
                    .map(userMapper::mapToUserDto)
                    .findFirst();
            sharer.ifPresent(item::setSharer);

            // Найдем и установим связанный с вещью запрос
            Optional<ItemRequest> request = searchResult.stream()
                    .filter(i -> i.getEntityId().equals(item.getId()))
                    .map(Item::getRequest)
                    .findFirst();
            request.ifPresent(itemRequest -> item.setRequest(itemRequestMapper.mapToItemRequestShortDto(itemRequest)));
            request.ifPresent(itemRequest -> item.getRequest()
                    .setRequestor(userMapper.mapToUserDto(request.get().getRequestor())));
        }
        log.debug("Найденная коллекция преобразована. Размер полученной коллекции {}", result.size());

        log.debug("Возврат результатов поиска по подстроке на уровень контроллера");
        return result;
    }

    @Override
    public ItemFullDto findById(Long itemId, Long ownerId) {
        log.debug("Поиск вещи по идентификатору на уровне сервиса");

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + ownerId + " не найден"));
        log.debug("Передан идентификатор владельца: {}", owner.getEntityId());

        Item searchResult = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
        log.debug("Передан id вещи: {}", itemId);

        if (!searchResult.getSharer().equals(owner)) {
            boolean isBooker = bookingRepository.existsByItemAndBooker(searchResult.getEntityId(), owner.getEntityId(),
                    LocalDateTime.now(), BookingStatus.APPROVED);
            isBooker =
                    isBooker || bookingRepository.existsByItemAndBooker(searchResult.getEntityId(), owner.getEntityId(),
                            LocalDateTime.now(), BookingStatus.WAITING);
            if (!isBooker) {
                throw new ValidationException("Пользователь не является владельцем вещи или ранее её не бронировал");
            }
        }

        ItemFullDto result = itemMapper.mapToFullDto(searchResult);

        if (searchResult.getRequest() != null) {
            ItemRequestFullDto itemRequestFullDto = itemRequestMapper.mapToItemRequestFullDto(
                    searchResult.getRequest());
            UserDto requestor = userMapper.mapToUserDto(searchResult.getRequest().getRequestor());

            List<Long> requestsIds = List.of(searchResult.getRequest().getEntityId());
            Collection<Item> itemsWithRequests = itemRepository.findByRequestEntityIdIn(requestsIds, SORT_ITEM_ID_ASC);

            Collection<ItemShortDto> items = new ArrayList<>();

            for (Item item : itemsWithRequests) {
                ItemShortDto itemShortDto = itemMapper.mapToShortDto(item);

                Optional<UserDto> sharer = itemsWithRequests.stream()
                        .filter(i -> i.equals(item))
                        .map(Item::getSharer)
                        .map(userMapper::mapToUserDto)
                        .findFirst();
                sharer.ifPresent(itemShortDto::setSharer);

                if (item.getRequest() != null) {
                    ItemRequestShortDto itemRequestShortDto = itemRequestMapper.mapToItemRequestShortDto(
                            item.getRequest());
                    UserDto shortRequestor = userMapper.mapToUserDto(item.getRequest().getRequestor());
                    itemRequestShortDto.setRequestor(shortRequestor);

                    itemShortDto.setRequest(itemRequestShortDto);
                }

                items.add(itemShortDto);
            }
            itemRequestFullDto.setItems(items);

            itemRequestFullDto.setRequestor(requestor);
            result.setRequest(itemRequestFullDto);
        }

        Optional<Booking> booking = bookingRepository.findTop1BookingByItemEntityIdAndEndDateIsAfterAndStatusIs(
                result.getId(), LocalDateTime.now(),
                BookingStatus.APPROVED, SORT_BOOKING_END_DESC);
        booking.ifPresent(b -> result.setLastBooking(bookingMapper.mapToShortDto(b)));

        booking = bookingRepository.findTop1BookingByItemEntityIdAndEndDateIsBeforeAndStatusIs(result.getId(),
                LocalDateTime.now(), BookingStatus.APPROVED, SORT_BOOKING_END_DESC);
        booking.ifPresent(b -> result.setNextBooking(bookingMapper.mapToShortDto(b)));

        result.setSharer(userMapper.mapToUserDto(owner));

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
        log.debug("Запрос на создание от имени пользователя с id: {}", userId);

        Item item = itemMapper.mapToItem(dto);
        log.debug("Полученная модель преобразована");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        item.setSharer(user);
        log.debug("Владелец создаваемой вещи найден и установлен");

        if (dto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(dto.getRequestId())
                    .orElseThrow(() -> new NotFoundException(
                            "По переданному id " + dto.getRequestId() + " запрос не найден"));
            item.setRequest(itemRequest);
        } else {
            log.debug("Вещь не связана с запросом");
        }

        item = itemRepository.save(item);
        log.debug("Новая вещь сохранена в хранилище");

        ItemShortDto result = itemMapper.mapToShortDto(item);

        result.setSharer(userMapper.mapToUserDto(item.getSharer()));

        if (item.getRequest() != null) {
            UserDto requestor = userMapper.mapToUserDto(item.getRequest().getRequestor());
            ItemRequestShortDto itemRequest = itemRequestMapper.mapToItemRequestShortDto(item.getRequest());
            itemRequest.setRequestor(requestor);
            result.setRequest(itemRequest);
        }
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

        comment = commentRepository.save(comment);
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
        log.debug("Передан идентификатор пользователя: {}", userId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + dto.getItemId() + " не найдена"));
        log.debug("Передан идентификатор обновляемой вещи: {}", itemId);

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

        result.setSharer(userMapper.mapToUserDto(item.getSharer()));

        if (item.getRequest() != null) {
            UserDto requestor = userMapper.mapToUserDto(item.getRequest().getRequestor());
            ItemRequestShortDto itemRequest = itemRequestMapper.mapToItemRequestShortDto(item.getRequest());
            itemRequest.setRequestor(requestor);
            result.setRequest(itemRequest);
        }
        log.debug("Измененная модель преобразована после сохранения изменений");

        log.debug("Возврат результатов изменения на уровень контроллера");
        return result;
    }

    @Override
    @Transactional
    public void delete(Long userId, Long itemId) {
        log.debug("Удаление вещи по идентификатору на уровне сервиса");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        log.debug("Запрос на удаление от пользователя с id: {}", user.getEntityId());

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
        log.debug("Передан идентификатор вещи: {}", item.getEntityId());

        if (!item.getSharer().equals(user)) {
            throw new ValidationException("Пользователь не является владельцем вещи или ранее её не бронировал");
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

        // Получим список связанных запросов по идентификаторам вещей
        List<Long> requestsIds = searchResult.stream()
                .map(Item::getRequest)
                .map(ItemRequest::getEntityId)
                .toList();
        Collection<Item> itemsWithRequests = itemRepository.findByRequestEntityIdIn(requestsIds, SORT_ITEM_ID_ASC);

        // Дополним коллекцию различными сведениями
        for (ItemFullDto item : result) {
            // Установим владельца вещи
            Optional<User> owner = searchResult.stream()
                    .filter(i -> i.getEntityId().equals(item.getId()))
                    .map(Item::getSharer)
                    .findFirst();
            owner.ifPresent(user -> item.setSharer(userMapper.mapToUserDto(user)));

            // Найдем последнее бронирование
            Optional<Booking> booking = bookingRepository.findTop1BookingByItemEntityIdAndEndDateIsAfterAndStatusIs(
                    item.getId(), LocalDateTime.now(),
                    BookingStatus.APPROVED, SORT_BOOKING_END_DESC);

            // Установим его
            booking.ifPresent(value -> item.setLastBooking(bookingMapper.mapToShortDto(value)));

            // Найдем следующее бронирование
            booking = bookingRepository.findTop1BookingByItemEntityIdAndEndDateIsBeforeAndStatusIs(item.getId(),
                    LocalDateTime.now(), BookingStatus.APPROVED, SORT_BOOKING_END_DESC);

            // Установим его
            booking.ifPresent(value -> item.setNextBooking(bookingMapper.mapToShortDto(value)));

            // Найдем все комментарии
            Collection<Comment> comments = commentRepository.findAllByItemEntityId(item.getId(),
                    SORT_COMMENT_CREATED_ASC);

            // Установим их
            item.setComments(comments.stream().map(commentMapper::mapToShortDto).toList());

            // Найдём запрос
            Optional<ItemRequest> itemRequest = searchResult.stream()
                    .filter(i -> i.getEntityId().equals(item.getId()))
                    .map(Item::getRequest)
                    .findFirst();

            // Установим его
            itemRequest.ifPresent(request -> {
                // Получим автора запроса
                UserDto requestor = userMapper.mapToUserDto(request.getRequestor());

                // Получим запрос
                ItemRequestFullDto itemRequestFullDto = itemRequestMapper.mapToItemRequestFullDto(request);

                // Установим автора запроса
                itemRequestFullDto.setRequestor(requestor);

                // Получим список вещей, связанных с запросом
                Collection<ItemShortDto> items = itemsWithRequests.stream()
                        .filter(i -> i.getRequest().getEntityId().equals(request.getEntityId()))
                        .map(itemMapper::mapToShortDto)
                        .toList();

                // Для каждой вещи из списка
                for (ItemShortDto it : items) {
                    // Найдём связанный запрос
                    Optional<ItemRequestShortDto> itemRequestShortDto = itemsWithRequests.stream()
                            .filter(i -> i.getEntityId().equals(it.getId()))
                            .map(Item::getRequest)
                            .findFirst()
                            .map(itemRequestMapper::mapToItemRequestShortDto);

                    itemRequestShortDto.ifPresent(it::setRequest);

                    // Найдём владельца
                    Optional<UserDto> sharer = itemsWithRequests.stream()
                            .filter(i -> i.getEntityId().equals(it.getId()))
                            .map(Item::getSharer)
                            .map(userMapper::mapToUserDto)
                            .findFirst();
                    // И установим его
                    sharer.ifPresent(it::setSharer);
                }

                // Установим заполненный список связанных вещей запросу
                itemRequestFullDto.setItems(items);

                // Свяжем запрос с вещью
                item.setRequest(itemRequestFullDto);
            });
        }

        return result;
    }
}
