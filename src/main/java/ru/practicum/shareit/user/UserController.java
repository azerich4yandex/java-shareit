package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

/**
 * Обработка HTTP-запросов для /users
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Обработка GET-запроса к /users
     *
     * @return коллекция {@link UserDto}
     */
    @GetMapping
    public ResponseEntity<Collection<UserDto>> findAll() {
        log.debug("Запрос всех пользователей на уровне контроллера");

        Collection<UserDto> result = userService.findAll();
        log.debug("На уровень контроллера вернулась коллекция размером {}", result.size());

        log.debug("Возврат результатов поиска на уровень клиента");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Обработка GET-запроса к /user/{id}
     *
     * @param userId идентификатор пользователя
     * @return экземпляр класса {@link UserDto}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable(name = "id") Long userId) {
        log.debug("Поиск пользователя по идентификатору на уровне контроллера");
        log.debug("Передан id пользователя: {}", userId);

        UserDto result = userService.findById(userId);
        log.debug("На уровень контроллера ввернулся экземпляр с id {}", result.getId());

        log.debug("Возврат результатов поиска по идентификатору на уровень клиента");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Обработка POST-запроса к /users
     *
     * @param dto несохраненный экземпляр класса {@link NewUserDto}
     * @return сохраненный экземпляр класса {@link UserDto}
     */
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody NewUserDto dto) {
        log.debug("Создание пользователя на уровне контроллера");

        UserDto result = userService.create(dto);
        log.debug("На уровень контроллера после создания вернулся экземпляр с id {}", result.getId());

        log.debug("Возврат результатов создания на уровень клиента");
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    /**
     * Обработка PATCH-запроса к /users/{id}
     *
     * @param userId идентификатор пользователя
     * @param dto обновляемая модель {@link UpdateUserDto}
     * @return сохраненная модель {@link UserDto}
     */
    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable(name = "id") Long userId,
                                              @RequestBody UpdateUserDto dto) {
        log.debug("Обновление пользователя на уровне контроллера");
        log.debug("Передан id обновляемого пользователя: {}", userId);

        UserDto result = userService.update(userId, dto);
        log.debug("На уровень контроллера после обновления вернулся экземпляр с id {}", result.getId());

        log.debug("Возврат результатов изменения на уровень клиента");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Обработка DELETE-запроса к /users/{id}
     *
     * @param userId идентификатор пользователя
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "id") Long userId) {
        log.debug("Удаление пользователя по идентификатору на уровне контроллера");
        log.debug("Передан идентификатор пользователя: {}", userId);

        userService.delete(userId);
        log.debug("На уровень контроллера вернулась информация об успешном удалении пользователя");

        log.debug("Возврат результатов удаления на уровень клиента");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
