package ru.practicum.shareit.user.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.commons.exceptions.NotFoundException;
import ru.practicum.shareit.commons.exceptions.ValueAlreadyUsedException;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Обработка данных на уровне сервиса UserService")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {

    @Autowired
    private final UserServiceImpl userService;

    @MockBean
    private final UserRepository userRepository;

    @MockBean
    private final ItemRequestRepository itemRequestRepository;

    @MockBean
    private final BookingRepository bookingRepository;

    @MockBean
    private final CommentRepository commentRepository;

    @MockBean
    private final ItemRepository itemRepository;

    private User user;
    private UserCreateDto userCreateDto;
    private UserUpdateDto userUpdateDto;

    private static Page<User> getPageFromList(List<User> list) {
        return new PageImpl<>(list, PageRequest.of(0, list.size()), list.size());
    }

    @BeforeEach
    void setUp() {
        Random random = new Random();

        user = User.builder()
                .entityId(Math.abs(random.nextLong()))
                .name("First User")
                .email("first@sysem.com")
                .build();

        userCreateDto = UserCreateDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();

        userUpdateDto = UserUpdateDto.builder()
                .userId(user.getEntityId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    @AfterEach
    void tearDown() {
        user = null;
        userCreateDto = null;
        userUpdateDto = null;
    }

    @DisplayName("Получение списка пользователей")
    @Test
    void getAllUsers() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Direction.ASC, "entityId"));

        when(userRepository.findAll(pageable))
                .thenReturn(getPageFromList(List.of(user)));

        Collection<UserDto> userList = userService.findAll(0, 10);
        assertNotNull(userList);
        assertFalse(userList.isEmpty());
        assertEquals(1, userList.size());

        Optional<UserDto> userDtoOpt = userList.stream().findFirst();
        assertTrue(userDtoOpt.isPresent());
        UserDto userDto = userDtoOpt.get();
        assertNotNull(userDto);
        assertNotNull(userDto.getId());
        assertEquals(user.getEntityId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @DisplayName("Поиск пользователя по идентификатору")
    @Test
    void findById() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        UserDto userDto = userService.findById(user.getEntityId());
        assertNotNull(userDto);
        assertNotNull(userDto.getId());
        assertEquals(user.getEntityId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @DisplayName("Вызов исключения  при поиске пользователя по идентификатору")
    @Test
    void findByIdWith404Exception() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findById(user.getEntityId()));
    }

    @DisplayName("Создание пользователя")
    @Test
    void createUser() {
        when(userRepository.existsByEmailIgnoreCase(anyString()))
                .thenReturn(false);
        when(userRepository.save(any()))
                .thenReturn(user);

        UserDto userDto = userService.create(userCreateDto);
        assertNotNull(userDto);
        assertEquals(user.getEntityId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @DisplayName("Вызов исключения ValueAlreadyUsedException при создании пользователя")
    @Test
    void createUserWith403Exception() {
        when(userRepository.existsByEmailIgnoreCase(anyString()))
                .thenReturn(true);

        assertThrows(ValueAlreadyUsedException.class, () -> userService.create(userCreateDto));
    }

    @DisplayName("Обновление пользователя")
    @Test
    void updateUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(userRepository.existsByEmailAndUserId(anyString(), anyLong()))
                .thenReturn(false);
        when(userRepository.save(any()))
                .thenReturn(user);

        UserDto userDto = userService.update(user.getEntityId(), userUpdateDto);
        assertNotNull(userDto);
        assertNotNull(userDto.getId());
        assertEquals(user.getEntityId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @DisplayName("Вызов исключения ValueAlreadyUsedException при обновлении пользователя")
    @Test
    void updateUserWith403Exception() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(userRepository.existsByEmailAndUserId(anyString(), anyLong()))
                .thenReturn(true);

        assertThrows(ValueAlreadyUsedException.class, () -> userService.update(user.getEntityId(), userUpdateDto));
    }

    @DisplayName("Вызов исключения NotFoundException при обновлении пользователя")
    @Test
    void updateUserWith404Exception() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.update(user.getEntityId(), userUpdateDto));
    }

    @DisplayName("Удаление пользователя по идентификатору")
    @Test
    void deleteUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        userRepository.deleteById(user.getEntityId());

        verify(userRepository).deleteById(user.getEntityId());
    }

    @DisplayName("Вызов исключения NotFoundException при удалении пользователя по идентификатору")
    @Test
    void deleteUserWith404Exception() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.delete(user.getEntityId()));
    }
}