package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {

    public static User mapToUser(NewUserDto dto) {
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }

    public static UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getEntityId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public static void updateUserFields(UpdateUserDto dto, User user) {
        if (dto.hasEmail()) {
            user.setEmail(dto.getEmail());
        }

        if (dto.hasName()) {
            user.setName(dto.getName());
        }
    }
}
