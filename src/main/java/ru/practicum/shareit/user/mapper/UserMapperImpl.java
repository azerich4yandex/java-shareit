package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Service
public class UserMapperImpl implements UserMapper {

    @Override
    public User mapToUser(UserCreateDto dto) {
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }

    @Override
    public UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getEntityId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    @Override
    public void updateUserFields(UserUpdateDto dto, User user) {
        if (dto.hasEmail()) {
            user.setEmail(dto.getEmail());
        }

        if (dto.hasName()) {
            user.setName(dto.getName());
        }
    }
}
