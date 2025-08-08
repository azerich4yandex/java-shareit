package ru.practicum.shareit.user.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

@Service
@Slf4j
public class UserMapperImpl implements UserMapper {

    @Override
    public User mapToUser(UserCreateDto dto) {
        log.debug("Преобразование данных из модели {} в модель {} для сохранения", UserCreateDto.class,
                User.class);
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }

    @Override
    public UserDto mapToUserDto(User user) {
        log.debug("Преобразование данных из модели {} в модель {}", User.class, UserDto.class);
        return UserDto.builder()
                .id(user.getEntityId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    @Override
    public void updateUserFields(UserUpdateDto dto, User user) {
        log.debug("Изменение полей в экземпляре класса {} на основе данных из экземпляра класса {}", User.class,
                UserUpdateDto.class);
        if (dto.hasEmail()) {
            log.debug("Будет изменен почтовый адрес");
            user.setEmail(dto.getEmail());
        }

        if (dto.hasName()) {
            log.debug("Будет изменено имя");
            user.setName(dto.getName());
        }
    }
}
