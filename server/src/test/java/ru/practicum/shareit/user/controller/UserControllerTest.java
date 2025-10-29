package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.commons.exceptions.NotFoundException;
import ru.practicum.shareit.commons.exceptions.ValueAlreadyUsedException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Обработка REST-запросов к /users")
@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    private UserDto userDto;
    private UserCreateDto userCreateDto;
    private UserUpdateDto userUpdateDto;

    @BeforeEach
    void setUp() {
        Random random = new Random();

        userDto = UserDto.builder()
                .id(Math.abs(random.nextLong()))
                .name("First User")
                .email("first@system.com")
                .build();

        userCreateDto = UserCreateDto.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();

        userUpdateDto = UserUpdateDto.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    @AfterEach
    void tearDown() {
        userDto = null;
        userCreateDto = null;
    }

    @DisplayName("Получение списка пользователей")
    @Test
    void findAll() throws Exception {
        when(userService.findAll(anyInt(), anyInt()))
                .thenReturn(List.of(userDto));

        mockMvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("from", "0")
                        .param("size", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())));
    }

    @DisplayName("Вызов исключения RuntimeException при получении списка пользователей")
    @Test
    void findAllWith403Exception() throws Exception {
        when(userService.findAll(anyInt(), anyInt()))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("from", "0")
                        .param("size", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andExpect(status().isInternalServerError());
    }

    @DisplayName("Поиск пользователя по идентификатору")
    @Test
    void findById() throws Exception {
        when(userService.findById(anyLong()))
                .thenReturn(userDto);

        mockMvc.perform(get("/users/" + userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @DisplayName("Вызов исключения NotFoundException при поиск пользователя по идентификатору")
    @Test
    void findByIdWith404Exception() throws Exception {
        when(userService.findById(anyLong()))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(get("/users/" + userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isNotFound());

    }

    @DisplayName("Создание пользователя")
    @Test
    void createUser() throws Exception {
        when(userService.create(any()))
                .thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.name", is(userDto.getName())));
    }

    @DisplayName("Вызов исключения ValueAlreadyUsedException при создании пользователя")
    @Test
    void createUserWith409Exception() throws Exception {
        when(userService.create(any()))
                .thenThrow(ValueAlreadyUsedException.class);

        mockMvc.perform(post("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isConflict());
    }

    @DisplayName("Вызов исключения RuntimeException при создании пользователя")
    @Test
    void createUserWith500Exception() throws Exception {
        when(userService.findById(anyLong()))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(post("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andExpect(status().isInternalServerError());
    }

    @DisplayName("Обновление пользователя по его идентификатору")
    @Test
    void updateUser() throws Exception {
        when(userService.update(anyLong(), any()))
                .thenReturn(userDto);

        mockMvc.perform(patch("/users/" + userDto.getId())
                        .content(objectMapper.writeValueAsString(userUpdateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }


    @DisplayName("Вызов исключения NotFoundException при обновлении пользователя по его идентификатору")
    @Test
    void updateUserWith404Exception() throws Exception {
        when(userService.update(anyLong(), any()))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(patch("/users/" + userDto.getId())
                        .content(objectMapper.writeValueAsString(userUpdateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isNotFound());
    }


    @DisplayName("Вызов исключения ValueAlreadyUsedException при обновлении пользователя по его идентификатору")
    @Test
    void updateUserWith409Exception() throws Exception {
        when(userService.update(anyLong(), any()))
                .thenThrow(ValueAlreadyUsedException.class);

        mockMvc.perform(patch("/users/" + userDto.getId())
                        .content(objectMapper.writeValueAsString(userUpdateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isConflict());
    }

    @DisplayName("Удаление пользователя по идентификатору")
    @Test
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/users/" + userDto.getId()))
                .andExpect(status().isOk());
        verify(userService).delete(userDto.getId());
    }
}