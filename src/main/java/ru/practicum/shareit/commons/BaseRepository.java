package ru.practicum.shareit.commons;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.commons.fields.IdentityField;

@RequiredArgsConstructor
@Slf4j
public class BaseRepository<T extends IdentityField> {

    protected final Map<Long, T> table = new HashMap<>();
    protected long idGenerator;

    /**
     * Метод возвращает коллекцию записей из хранилища
     *
     * @return коллекция записей
     */
    protected Collection<T> findMany() {
        log.debug("Поиск всех записей на уровне хранилища");

        Collection<T> result = table.values();
        log.debug("На уровне хранилища получена коллекция размером {}", result.size());

        log.debug("Возврат результатов поиска на уровень репозитория");
        return result;
    }

    /**
     * Метод возвращает экземпляр класса по идентификатору
     *
     * @param entryId идентификатор записи
     * @return запись из хранилища
     */
    protected Optional<T> findEntry(Long entryId) {
        log.debug("Поиск записи по идентификатору на уровне хранилища");
        log.debug("Передан id записи: {}", entryId);

        T result = table.get(entryId);

        log.debug("Возврат результата поиска на уровень репозитория");
        return Optional.of(result);
    }

    /**
     * Метод добавляет в хранилище запись
     *
     * @param entry несохраненный экземпляр класса для записи
     * @return сохраненный в хранилище экземпляр класса
     */
    protected T insertEntry(T entry) {
        log.debug("Создание записи на уровне хранилища");

        entry.setEntityId(getNextId());
        log.debug("Для сохраняемой записи сгенерирован новый id: {}. Значение присвоено записи", entry.getEntityId());

        table.put(entry.getEntityId(), entry);
        log.debug("Запись сохранена в хранилище");

        log.debug("Возврат результатов сохранения на уровень репозитория");
        return entry;
    }

    /**
     * Метод обновляет в хранилище запись
     *
     * @param entry несохраненный экземпляр класса для сохранения
     */
    protected void updateEntry(T entry) {
        log.debug("Обновление записи на уровне хранилища");

        table.put(entry.getEntityId(), entry);
        log.debug("Изменения сохранены в хранилище");

        log.debug("Возврат результатов обновления на уровень репозитория");
    }

    /**
     * Метод удаляет из хранилища запись по ее идентификатору
     *
     * @param entryId идентификатор записи
     */
    protected void deleteEntry(Long entryId) {
        log.debug("Удаление записи на уровне хранилища");
        log.debug("Передан идентификатор записи: {}", entryId);

        table.remove(entryId);
        log.debug("Запись с id {} удалена из хранилища", entryId);

        log.debug("Возврат результатов удаления на уровень репозитория");
    }

    /**
     * Метод возвращает следующее значение идентификатора
     *
     * @return следующее значение id
     */
    private long getNextId() {
        return ++idGenerator;
    }
}
