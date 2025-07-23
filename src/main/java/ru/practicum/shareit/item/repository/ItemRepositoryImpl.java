package ru.practicum.shareit.item.repository;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.commons.BaseRepository;
import ru.practicum.shareit.item.model.Item;

@Repository
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
        log.debug("Из хранилища получена коллекция размером {}", result.size());

        log.debug("Возврат результатов поиска на уровень сервиса");
        return List.of();
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

        Item result = insertEntry(item);
        log.debug("На уровень репозитория после сохранения вернулась вещь с id: {}", result.getEntityId());

        log.debug("Возврат результата сохранения на уровень сервиса");
        return result;
    }

    @Override
    public Item update(Item item) {
        log.debug("Обновление вещи на уровне репозитория");

        updateEntry(item);
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
