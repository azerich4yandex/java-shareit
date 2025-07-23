package ru.practicum.shareit.user.repository;

import java.util.Collection;
import java.util.Optional;
import ru.practicum.shareit.user.model.User;

public interface UserRepository {

    /**
     * Метод возвращает коллекцию экземпляров класса {@link User}
     *
     * @return коллекция экземпляров класса {@link User}
     */
    Collection<User> findAll();

    /**
     * Метод возвращает экземпляр класса {@link User}, полученный из хранилища
     *
     * @param userId идентификатор пользователя
     * @return экземпляр класса {@link User}
     */
    Optional<User> findById(Long userId);

    /**
     * Метод передает для сохранения в хранилище экземпляр класса {@link User}
     *
     * @param user несохраненный экземпляр класса {@link User}
     * @return сохраненный экземпляр класса {@link User}
     */
    User create(User user);

    /**
     * Метод передает для обновления в хранилище экземпляр класса {@link User}
     *
     * @param user экземпляр класса {@link User} с несохраненными изменениями
     * @return экземпляр класса {@link User} с сохраненными изменениями
     */
    User update(User user);

    /**
     * Метод удаляет из хранилища экземпляр класса {@link User} по переданному идентификатору
     *
     * @param userId идентификатор пользователя
     */
    void delete(Long userId);
}
