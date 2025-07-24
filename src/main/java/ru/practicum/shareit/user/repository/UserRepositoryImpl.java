package ru.practicum.shareit.user.repository;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.commons.BaseRepository;
import ru.practicum.shareit.user.model.User;

@Component
@Slf4j
public class UserRepositoryImpl extends BaseRepository<User> implements UserRepository {

    @Override
    public Collection<User> findAll() {
        log.debug("Запрос всех пользователей на уровне репозитория");

        Collection<User> result = findMany().stream()
                .sorted(Comparator.comparing(User::getEntityId))
                .toList();
        log.debug("Из хранилища получена коллекция размером {}", result.size());

        log.debug("Возврат результатов поиска на уровень сервиса");
        return result;
    }

    @Override
    public Optional<User> findById(Long userId) {
        log.debug("Поиск пользователя по идентификатору на уровне репозитория");
        log.debug("Передан id пользователя: {}", userId);

        Optional<User> result = findEntry(userId);

        if (result.isPresent()) {
            log.debug("На уровень репозитория вернулся пользователь с id: {}", result.get().getEntityId());
        } else {
            log.debug("На уровень репозитория вернулось пустое значение");
        }

        log.debug("Возврат результата поиска на уровень репозитория");
        return result;
    }

    @Override
    public User create(User user) {
        log.debug("Создание пользователя на уровне репозитория");

        user.setEntityId(getNextId());
        User result = insertEntry(user.getEntityId(), user);
        log.debug("На уровень репозитория после сохранения вернулся пользователь с id: {}", result.getEntityId());

        log.debug("Возврат результатов сохранения на уровень сервиса");
        return result;
    }

    @Override
    public User update(User user) {
        log.debug("Обновление пользователя на уровне репозитория");

        updateEntry(user.getEntityId(), user);
        log.debug("На уровень репозитория после обновления вернулся пользователь с id: {}", user.getEntityId());

        log.debug("Возврат результатов обновления на уровень сервиса");
        return user;
    }

    @Override
    public void delete(Long userId) {
        log.debug("Удаление пользователя по идентификатору на уровне репозитория");
        log.debug("Передан идентификатор пользователя: {}", userId);

        deleteEntry(userId);
        log.debug("На уровень репозитория вернулась информация об успешном удалении пользователя из хранилища");
    }
}
