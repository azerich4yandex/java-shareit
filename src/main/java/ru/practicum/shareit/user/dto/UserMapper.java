package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

public final class UserMapper {

    public static User mapToUser(NewUserDto dto) {
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }

    public static UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .userId(user.getEntityId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static void updateUserFields(UpdateUserDto dto, User user) {
        if (dto.hasName()) {
            user.setName(dto.getName());
        }

        if (dto.hasEmail()) {
            user.setEmail(dto.getEmail());
        }
    }
}
