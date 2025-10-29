package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

public interface UserMapper {

    /**
     * Метод преобразует {@link UserCreateDto} в {@link User}
     *
     * @param dto экземпляр класса {@link UserCreateDto}
     * @return преобразованный экземпляр класса {@link User}
     */
    User mapToUser(UserCreateDto dto);

    /**
     * Метод преобразует экземпляр класса {@link User} в {@link UserDto}
     *
     * @param user экземпляр класса {@link User}
     * @return преобразованный экземпляр класса {@link UserDto}
     */
    UserDto mapToUserDto(User user);

    /**
     * Метод дополняет поля класса {@link User}, если они заполнены в экземпляре класса {@link UserUpdateDto}
     *
     * @param dto источник изменений
     * @param user приемник изменений
     */
    void updateUserFields(UserUpdateDto dto, User user);
}
