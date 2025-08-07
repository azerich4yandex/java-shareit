package ru.practicum.shareit.item.service;

import jakarta.validation.ValidationException;
import java.util.ArrayList;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.commons.exceptions.NotFoundException;
import ru.practicum.shareit.commons.exceptions.UserIsNotSharerException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@RequiredArgsConstructor
@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Override
    public Collection<ItemDto> findAll(Long userId) {
        log.debug("Запрос всех вещей на уровне сервиса");

        if (userId == null) {
            throw new ValidationException("Атрибут \"X-Sharer-User-Id\" не найден в заголовке");
        }
        log.debug("Запрос от пользователя с id: {}", userId);

        Sort sort = Sort.by(Direction.ASC, "entityId");
        Collection<Item> searchResult = itemRepository.findAllBySharerEntityId(userId, sort);
        log.debug("Из репозитория получена коллекция размером {}", searchResult.size());

        Collection<ItemDto> result = searchResult.stream().map(itemMapper::mapToItemDto).toList();
        log.debug("Полученная коллекция преобразована. Размер полученной коллекции: {}", result.size());

        log.debug("Возврат результатов поиска на уровень контроллера");
        return result;
    }

    @Override
    public Collection<ItemDto> findByText(String text) {
        log.debug("Поиск вещей по вхождению подстроки на уровне сервиса");

        if (text == null || text.trim().isBlank()) {
            log.debug("Передано пустое значение подстроки. Возвращаем пустую коллекцию на уровень контроллера");
            return new ArrayList<>();
        }
        log.debug("Передана подстрока: {}", text);

        Collection<Item> searchResult = itemRepository.findAllByNameAndAvailable(text, true);
        log.debug("На уровне сервиса получен результат поиска по подстроке размером {}", searchResult.size());

        Collection<ItemDto> result = searchResult.stream()
                .map(itemMapper::mapToItemDto)
                .toList();
        log.debug("Найденная коллекция преобразована. Размер полученной коллекции {}", result.size());

        log.debug("Возврат результатов поиска по подстроке на уровень контроллера");
        return result;
    }

    @Override
    public ItemDto findById(Long itemId) {
        log.debug("Поиск вещи по идентификатору на уровне сервиса");

        if (itemId == null) {
            throw new ValidationException("Id вещи должен быть указан");
        }
        log.debug("Передан id вещи: {}", itemId);

        Item searchResult = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
        log.debug("На уровне хранилища найден пользователь с id {}", searchResult.getEntityId());

        ItemDto result = itemMapper.mapToItemDto(searchResult);
        log.debug("Полученная вещь преобразована");

        log.debug("Возврат результатов поиска по id на уровень контроллера");
        return result;
    }

    @Override
    public ItemDto create(Long userId, ItemCreateDto dto) {
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

        ItemDto result = itemMapper.mapToItemDto(item);
        log.debug("Сохраненная модель преобразована");

        log.debug("Возврат результатов сохранения на уровень контроллера");
        return result;
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemUpdateDto dto) {
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

        ItemDto result = itemMapper.mapToItemDto(item);
        log.debug("Измененная модель преобразована после сохранения изменений");

        log.debug("Возврат результатов изменения на уровень контроллера");
        return result;
    }

    @Override
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

        ItemDto dto = findById(itemId);
        log.debug("Вещь с id {} для удаления найдена в хранилище", dto.getId());

        if (!dto.getSharer().getId().equals(user.getEntityId())) {
            throw new ValidationException("Пользователь не является владельцем вещи");
        }

        itemRepository.deleteById(dto.getId());
        log.debug("На уровень сервиса вернулась информация об успешном удалении вещи из хранилища");

        log.debug("Возврат результатов удаления на уровень контроллера");
    }
}
