package ru.practicum.shareit.item.service;

import jakarta.validation.ValidationException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.commons.BaseRepository;
import ru.practicum.shareit.commons.exceptions.NotFoundException;
import ru.practicum.shareit.commons.exceptions.UserIsNotSharerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@RequiredArgsConstructor
@Service
@Slf4j
public class ItemServiceImpl extends BaseRepository<Item> implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Collection<ItemDto> findAll(Long userId) {
        log.debug("Запрос всех вещей на уровне сервиса");

        if (userId == null) {
            throw new ValidationException("Атрибут \"X-Sharer-User-Id\" не найден в заголовке");
        }
        log.debug("Запрос от пользователя с id: {}", userId);

        Collection<Item> searchResult = itemRepository.findAll(userId);
        log.debug("Из репозитория получена коллекция размером {}", searchResult.size());

        Collection<ItemDto> result = searchResult.stream().map(ItemMapper::mapToItemDto).toList();
        log.debug("Полученная коллекция преобразована. Размер полученной коллекции: {}", result.size());

        log.debug("Возврат результатов поиска на уровень контроллера");
        return result;
    }

    @Override
    public Collection<ItemDto> findByText(String text) {
        log.debug("Поиск вещей по вхождению подстроки на уровне сервиса");

        if (text == null || text.strip().isBlank()) {
            log.debug("Передано пустое значение подстроки. Возвращаем пустую коллекцию на уровень контроллера");
            return new ArrayList<>();
        }
        log.debug("Передана подстрока: {}", text);

        Collection<Item> searchResult = itemRepository.findByText(text);
        log.debug("На уровне сервиса получен результат поиска по подстроке размером {}", searchResult.size());

        Collection<ItemDto> result = searchResult.stream()
                .map(ItemMapper::mapToItemDto)
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

        ItemDto result = ItemMapper.mapToItemDto(searchResult);
        log.debug("Полученная вещь преобразована");

        log.debug("Возврат результатов поиска по id на уровень контроллера");
        return result;
    }

    @Override
    public ItemDto create(Long userId, NewItemDto dto) {
        log.debug("Создание вещи на уровне сервиса");

        if (userId == null) {
            throw new ValidationException("Атрибут \"X-Sharer-User-Id\" не найден в заголовке");
        }
        log.debug("Запрос на создание от имени пользователя с id: {}", userId);

        Item item = ItemMapper.mapToItem(dto);
        log.debug("Полученная модель преобразована");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        item.setSharerId(user.getEntityId());

        log.debug("Владелец создаваемой вещи найден и установлен");

        log.debug("Валидация преобразованной модели");
        validate(item);
        log.debug("Валидация преобразованной модели завершена");

        item = itemRepository.create(item);
        log.debug("Новая вещь сохранена в хранилище");

        ItemDto result = ItemMapper.mapToItemDto(item);
        log.debug("Сохраненная модель преобразована");

        log.debug("Возврат результатов сохранения на уровень контроллера");
        return result;
    }

    @Override
    public ItemDto update(Long userId, Long itemId, UpdateItemDto dto) {
        log.debug("Обновление вещи на уровне сервиса");

        if (userId == null) {
            throw new ValidationException("Атрибут \"X-Sharer-User-Id\" не найден в заголовке");
        }

        if (itemId == null) {
            throw new ValidationException("Id вещи должен быть указан");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + dto.getItemId() + " не найдена"));

        if (!item.getSharerId().equals(userId)) {
            throw new UserIsNotSharerException(
                    "Пользователь с id " + userId + " не является владельцем вещи с id " + item.getEntityId());
        }
        log.debug("В хранилище найдена вещь для обновления с id {}", item.getEntityId());

        dto.setItemId(itemId);
        ItemMapper.updateItemFields(dto, item);
        log.debug("Измененная и полученная модели преобразованы");

        log.debug("Валидация обновленной преобразованной модели");
        validate(item);
        log.debug("Валидация обновленной преобразованной модели");

        item = itemRepository.update(item);
        log.debug("Измененная модель сохранения в хранилище");

        ItemDto result = ItemMapper.mapToItemDto(item);
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

        itemRepository.delete(dto.getId());
        log.debug("На уровень сервиса вернулась информация об успешном удалении вещи из хранилища");

        log.debug("Возврат результатов удаления на уровень контроллера");
    }

    /**
     * Метод проверяет правильность заполнения ключевых полей перед внесением изменений в хранилище
     *
     * @param item экземпляр класса {@link Item}
     */
    private void validate(Item item) {
        // Валидация доступности
        validateAvailable(item.getAvailable());

        // Валидация наименования вещи
        validateString(item.getName(), "Имя");

        // Валидация описания вещи
        validateString(item.getDescription(), "Описание");
    }

    public void validateAvailable(Boolean available) {
        log.debug("Валидация доступности на уровне сервиса");

        if (available == null) {
            throw new ValidationException("Признак доступности должен быть указан");
        }
        log.debug("Передано корректное значение доступности: {}", available);

        log.debug("Валидация доступности на уровне сервиса завершена");
    }

    /**
     * Метод проверяет правильность заполнения строковых полей вещи
     *
     * @param value строковое значение поля
     * @param fieldName наименование поля
     */
    private void validateString(String value, String fieldName) {
        log.debug("Валидация поля \"{}\" на уровне сервиса", fieldName);

        // Поле не должно быть пустым
        if (value == null || value.strip().isBlank()) {
            throw new ValidationException(fieldName + " должно быть указано");
        }

        // Подводим итоги валидации
        log.debug("Передано корректное значение поля \"{}\": {}", fieldName, value);
    }
}
