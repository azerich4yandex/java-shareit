package ru.practicum.shareit.item.repository;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.commons.BaseRepository;
import ru.practicum.shareit.item.model.Item;

@Component
@Slf4j
public class ItemRepositoryImpl extends BaseRepository<Item> implements ItemRepository {

    @Override
    public Collection<Item> findAll(Long userId) {
        log.debug("Запрос всех вещей на уровне репозитория");
        log.debug("Запрос от пользователя с id: {}", userId);

        Collection<Item> result = findMany().stream()
                .filter(item -> item.getSharerId().equals(userId))
                .sorted(Comparator.comparing(Item::getEntityId))
                .toList();
        log.debug("На уровне репозитория получена коллекция размером {}", result.size());

        log.debug("Возврат результатов поиска на уровень сервиса");
        return result;
    }

    @Override
    public Collection<Item> findByText(String text) {
        log.debug("Поиск вещей по подстроке на уровне репозитория");
        log.debug("Передана подстрока: {}", text);

        Collection<Item> result = findMany().stream()
                .filter(item -> item.getAvailable() &&
                        (item.getName().toUpperCase().contains(text.toUpperCase())
                                || item.getDescription().toUpperCase().contains(text.toUpperCase())))
                .toList();
        log.debug("На уровне репозитория найден а коллекция размером {}", result.size());

        log.debug("Возврат результатов поиска по подстроке на уровень сервиса");
        return result;
    }

    @Override
    public Optional<Item> findById(Long itemId) {
        log.debug("Поиск вещи по идентификатору на уровне репозитория");
        log.debug("Передан id вещи: {}", itemId);

        Optional<Item> result = findEntry(itemId);

        if (result.isPresent()) {
            log.debug("На уровень репозитория вернулась запись с id: {}", result.get().getEntityId());
        } else {
            log.debug("На уровень репозитория вернулось пустое значение");
        }

        log.debug("Возврат результата поиска на уровень репозитория");
        return result;
    }

    @Override
    public Item create(Item item) {
        log.debug("Создание вещи на уровне репозитория");

        item.setEntityId(getNextId());
        Item result = insertEntry(item.getEntityId(), item);
        log.debug("На уровень репозитория после сохранения вернулась вещь с id: {}", result.getEntityId());

        log.debug("Возврат результата сохранения на уровень сервиса");
        return result;
    }

    @Override
    public Item update(Item item) {
        log.debug("Обновление вещи на уровне репозитория");

        item.setEntityId(getNextId());
        updateEntry(item.getEntityId(), item);
        log.debug("На уровень репозитория после обновления вернулась вещь с id: {}", item.getEntityId());

        log.debug("Возврат результатов обновления на уровень сервиса");
        return item;
    }

    @Override
    public void delete(Long itemId) {
        log.debug("Удаление вещи по идентификатору на уровне репозитория");
        log.debug("Передан идентификатор вещи: {}", itemId);

        deleteEntry(itemId);
        log.debug("На уровень репозитория вернулась информация об успешном удалении вещи из хранилища");

        log.debug("Возврат результатов удаления на уровень сервиса");
    }
}
