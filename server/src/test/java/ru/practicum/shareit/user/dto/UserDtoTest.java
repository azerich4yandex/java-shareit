package ru.practicum.shareit.user.dto;

import java.util.Random;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Проверка преобразования DTO пользователей в JSON-объекты")
@JsonTest
class UserDtoTest {

    @Autowired
    private JacksonTester<UserCreateDto> userCreateDtoJacksonTester;

    @Autowired
    private JacksonTester<UserDto> userDtoJacksonTester;

    @Autowired
    private JacksonTester<UserUpdateDto> userUpdateDtoJacksonTester;

    private UserCreateDto userCreateDto;
    private UserDto userDto;
    private UserUpdateDto userUpdateDto;

    @BeforeEach
    void setUp() {
        Random random = new Random();

        userCreateDto = UserCreateDto.builder()
                .name("User Test")
                .email("user@system.com")
                .build();

        userDto = UserDto.builder()
                .id(Math.abs(random.nextLong()))
                .name(userCreateDto.getName())
                .email(userCreateDto.getEmail())
                .build();

        userUpdateDto = UserUpdateDto.builder()
                .userId(userDto.getId())
                .name(userDto.getName())
                .name(userDto.getEmail())
                .build();
    }

    @AfterEach
    void tearDown() {
        userCreateDto = null;
        userDto = null;
        userUpdateDto = null;
    }

    @DisplayName("Проверка преобразования в JSON-объект для класса UserCreateDto")
    @Test
    void userCreateDtoJacksonTesterTest() throws Exception {
        JsonContent<UserCreateDto> jsonContent = userCreateDtoJacksonTester.write(userCreateDto);

        assertThat(jsonContent).hasJsonPath("$.name");
        assertThat(jsonContent).extractingJsonPathStringValue("$.name")
                .isEqualTo(userCreateDto.getName());
        assertThat(jsonContent).hasJsonPath("$.email");
        assertThat(jsonContent).extractingJsonPathStringValue("$.email")
                .isEqualTo(userCreateDto.getEmail());
    }

    @DisplayName("Проверка преобразования в JSON-объект для класса UserDto")
    @Test
    void userDtoJacksonTesterTest() throws Exception {
        JsonContent<UserDto> jsonContent = userDtoJacksonTester.write(userDto);

        assertThat(jsonContent).hasJsonPath("$.id");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.id")
                .isEqualTo(userDto.getId());
        assertThat(jsonContent).hasJsonPath("$.name");
        assertThat(jsonContent).extractingJsonPathStringValue("$.name")
                .isEqualTo(userDto.getName());
        assertThat(jsonContent).hasJsonPath("$.email");
        assertThat(jsonContent).extractingJsonPathStringValue("$.email")
                .isEqualTo(userDto.getEmail());
    }

    @DisplayName("Проверка преобразования в JSON-объект для класса UserUpdateDto")
    @Test
    void userUpdateDtoJacksonTesterTest() throws Exception {
        JsonContent<UserUpdateDto> jsonContent = userUpdateDtoJacksonTester.write(userUpdateDto);

        assertThat(jsonContent).hasJsonPath("$.userId");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.userId")
                .isEqualTo(userUpdateDto.getUserId());
        assertThat(jsonContent).hasJsonPath("$.name");
        assertThat(jsonContent).extractingJsonPathStringValue("$.name")
                .isEqualTo(userUpdateDto.getName());
        assertThat(jsonContent).hasJsonPath("$.email");
        assertThat(jsonContent).extractingJsonPathStringValue("$.email")
                .isEqualTo(userUpdateDto.getEmail());
    }
}