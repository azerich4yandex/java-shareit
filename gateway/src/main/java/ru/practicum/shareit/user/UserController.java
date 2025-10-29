package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.commons.exceptions.IncorrectDataException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

/**
 * Обработка HTTP-запросов для /users
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserClient userClient;

    /**
     * Обработка GET-запроса к /users
     *
     * @param from номер начального элемента коллекции
     * @param size максимальный размер возвращаемой коллекции
     * @return коллекция пользователей
     */
    @GetMapping
    public ResponseEntity<Object> findAll(
            @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        log.info("Запрос всех пользователей на уровне клиента");

        return userClient.getUsers(from, size);
    }

    /**
     * Обработка GET-запроса к /user/{id}
     *
     * @param userId идентификатор пользователя
     * @return экземпляр пользователя
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable(name = "id") Long userId) {
        log.info("Поиск пользователя по идентификатору на уровне клиента");

        if (userId == null) {
            throw new IncorrectDataException("Идентификатор пользователя должен быт указан");
        }
        log.info("Передан id пользователя: {}", userId);

        return userClient.getUser(userId);
    }

    /**
     * Обработка POST-запроса к /users
     *
     * @param dto несохраненный экземпляр класса {@link UserCreateDto}
     * @return сохраненная модель пользователя
     */
    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @RequestBody UserCreateDto dto) {
        log.info("Создание пользователя на уровне клиента");
        log.debug("Передана модель DTO для создания пользователя: {}", dto);

        return userClient.addUser(dto);
    }

    /**
     * Обработка PATCH-запроса к /users/{id}
     *
     * @param userId идентификатор пользователя
     * @param dto обновляемая модель {@link UserUpdateDto}
     * @return сохраненная модель пользователя
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable(name = "id") Long userId,
                                             @Valid @RequestBody UserUpdateDto dto) {
        log.info("Обновление пользователя на уровне клиента");
        log.debug("Передана модель DTO для обновления вещи: {}", dto);

        if (userId == null) {
            throw new IncorrectDataException("Идентификатор пользователя должен быть указан");
        }
        log.info("Передан id обновляемого пользователя: {}", userId);

        return userClient.updateUser(userId, dto);
    }

    /**
     * Обработка DELETE-запроса к /users/{id}
     *
     * @param userId идентификатор пользователя
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable(name = "id") Long userId) {
        log.info("Удаление пользователя по идентификатору на уровне клиента");

        if (userId == null) {
            throw new IncorrectDataException("Идентификатор пользователя должен быть указан");
        }
        log.info("Передан идентификатор пользователя: {}", userId);

        return userClient.deleteUser(userId);
    }
}
