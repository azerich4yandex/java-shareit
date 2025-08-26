package ru.practicum.shareit.user.mapper;

import java.util.Random;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Проверка работы маппера UserMapper")
class UserMapperImplTest {

    private UserMapperImpl userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapperImpl();
    }

    @AfterEach
    void tearDown() {
        userMapper = null;
    }

    @DisplayName("Проверка преобразования из UserCreateDto в User")
    @Test
    void mapToUser() {
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .name("User Test")
                .email("user@system.com")
                .build();

        User user = userMapper.mapToUser(userCreateDto);
        assertNotNull(user);
        assertEquals(userCreateDto.getName(), user.getName());
        assertEquals(userCreateDto.getEmail(), user.getEmail());
    }

    @DisplayName("Проверка преобразования из User в UserDto")
    @Test
    void mapToUserDto() {
        Random random = new Random();

        User user = User.builder()
                .entityId(Math.abs(random.nextLong()))
                .name("User Test")
                .email("user@system.com")
                .build();

        UserDto userDto = userMapper.mapToUserDto(user);
        assertNotNull(userDto);
        assertEquals(user.getEntityId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @DisplayName("Проверка преобразования из UserUpdateDto в User")
    @Test
    void updateUserFields() {
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .email("user@system.com")
                .name("User Test")
                .build();

        User user = new User();

        userMapper.updateUserFields(userUpdateDto, user);
        assertNotNull(userUpdateDto);
        assertEquals(userUpdateDto.getEmail(), user.getEmail());
        assertEquals(userUpdateDto.getName(), user.getName());
    }
}