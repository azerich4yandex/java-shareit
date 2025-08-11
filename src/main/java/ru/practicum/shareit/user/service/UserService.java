package ru.practicum.shareit.user.service;

import java.util.Collection;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
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
     * Метод проверяет и передает для сохранения полученный экземпляр класса {@link UserCreateDto} и возвращает его с
     * заполненными полями после сохранения
     *
     * @param dto несохраненный экземпляр класса {@link UserCreateDto}
     * @return сохраненный экземпляр класса {@link UserDto}
     */
    UserDto create(UserCreateDto dto);

    /**
     * Метод проверяет и передает для обновления полученный экземпляр класса {@link UserUpdateDto} и возвращает его с
     * обновленными полями после обновления
     *
     * @param dto несохраненный экземпляр класса {@link UserUpdateDto}
     * @return сохраненный экземпляр класса {@link UserDto}
     */
    UserDto update(Long userId, UserUpdateDto dto);

    /**
     * Метод проверяет и передает для удаления пользователя по его идентификатору
     *
     * @param userId идентификатор пользователя
     */
    void delete(Long userId);
}
