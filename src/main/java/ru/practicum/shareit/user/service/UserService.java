package ru.practicum.shareit.user.service;

import java.util.Collection;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

    /**
     * Метод возвращает коллекцию пользователей
     *
     * @return коллекция {@link UserDto}
     */
    Collection<UserDto> findAll();

    /**
     * Метод возвращает экземпляр класса {@link UserDto} по переданному идентификатору
     *
     * @param userId идентификатор пользователя
     * @return экземпляр класса {@link UserDto}
     */
    UserDto findById(Long userId);

    /**
     * Метод проверяет и передает для сохранения полученный экземпляр класса {@link NewUserDto} и возвращает его с
     * заполненными полями после сохранения
     *
     * @param dto несохраненный экземпляр класса {@link NewUserDto}
     * @return сохраненный экземпляр класса {@link UserDto}
     */
    UserDto create(NewUserDto dto);

    /**
     * Метод проверяет и передает для обновления полученный экземпляр класса {@link UpdateUserDto} и возвращает его с
     * обновленными полями после обновления
     *
     * @param dto несохраненный экземпляр класса {@link UpdateUserDto}
     * @return сохраненный экземпляр класса {@link UserDto}
     */
    UserDto update(Long userId, UpdateUserDto dto);

    /**
     * Метод проверяет и передает для удаления пользователя по его идентификатору
     *
     * @param userId идентификатор пользователя
     */
    void delete(Long userId);
}
