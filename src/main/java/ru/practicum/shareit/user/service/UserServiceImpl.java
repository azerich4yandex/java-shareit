package ru.practicum.shareit.user.service;

import jakarta.validation.ValidationException;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.commons.exceptions.NotFoundException;
import ru.practicum.shareit.commons.exceptions.ValueAlreadyUsedException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public Collection<UserDto> findAll() {
        log.debug("Запрос всех пользователей на уровне сервиса");

        Collection<User> searchResult = userRepository.findAll();
        log.debug("Из репозитория получена коллекция размером {}", searchResult.size());

        Collection<UserDto> result = searchResult.stream().map(userMapper::mapToUserDto).toList();
        log.debug("Полученная коллекция преобразована. Размер коллекции после преобразования: {}", result.size());

        log.debug("Возврат результатов поиска на уровень контроллера");
        return result;
    }

    @Override
    public UserDto findById(Long userId) {
        log.debug("Поиск пользователя по идентификатору на уровне сервиса");

        if (userId == null) {
            throw new ValidationException("Id пользователя должен быть указан");
        }
        log.debug("Передан id пользователя: {}", userId);

        User searchResult = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        log.debug("На уровне хранилища найден пользователь с id {}", searchResult.getEntityId());

        UserDto result = userMapper.mapToUserDto(searchResult);
        log.debug("Полученный пользователь преобразован");

        log.debug("Возврат результатов поиска по id на уровень контроллера");
        return result;
    }

    @Override
    @Transactional
    public UserDto create(UserCreateDto dto) {
        log.debug("Создание пользователя на уровне сервиса");

        User user = userMapper.mapToUser(dto);
        log.debug("Полученная модель преобразована");

        log.debug("Валидация преобразованной модели");
        validate(user);
        log.debug("Валидация преобразованной модели завершена");

        user = userRepository.save(user);
        log.debug("Новый пользователь сохранен в хранилище");

        UserDto result = userMapper.mapToUserDto(user);
        log.debug("Сохраненная модель преобразована");

        log.debug("Возврат результатов создания на уровень контроллера");
        return result;
    }

    @Override
    @Transactional
    public UserDto update(Long userId, UserUpdateDto dto) {
        log.debug("Обновление пользователя на уровне сервиса");

        if (userId == null) {
            throw new ValidationException("Id пользователя должен быть указан");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + dto.getUserId() + " не найден"));
        log.debug("В хранилище найден пользователь для обновления с id {}", user.getEntityId());

        dto.setUserId(userId);
        userMapper.updateUserFields(dto, user);
        log.debug("Измененная и полученная модели преобразованы");

        log.debug("Валидация обновленной преобразованной модели");
        validate(user);
        log.debug("Валидация обновленной преобразованной модели завершена");

        user = userRepository.save(user);
        log.debug("Измененная модель сохранена в хранилище");

        UserDto result = userMapper.mapToUserDto(user);
        log.debug("Измененная модель преобразована после сохранения изменений");

        log.debug("Возврат результатов изменения на уровень контроллера");
        return result;
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        log.debug("Удаление пользователя по идентификатору на уровне сервиса");

        if (userId == null) {
            throw new ValidationException("Id пользователя должен быть указан");
        }
        log.debug("Передан идентификатор пользователя: {}", userId);

        UserDto dto = findById(userId);
        log.debug("Пользователь с id {} для удаления найден в хранилище", dto.getId());

        userRepository.deleteById(dto.getId());
        log.debug("На уровень сервиса вернулась информация об успешном удалении пользователя из хранилища");

        log.debug("Возврат результатов удаления на уровень контроллера");
    }

    /**
     * Метод проверяет правильность заполнения ключевых полей перед внесением изменений в хранилище
     *
     * @param user экземпляр класса {@link User}
     */
    private void validate(User user) {
        // Валидация почты пользователя
        validateEmail(user);
    }

    /**
     * Метод проверяет правильность заполнения почтового адреса пользователя
     *
     * @param user экземпляр класса {@link User}
     */
    private void validateEmail(User user) {
        log.debug("Валидация почты пользователя на уровне сервиса");

        // Почта не должна использоваться другими пользователями
        boolean exists;
        if (user.getEntityId() == null) {
            exists = userRepository.existsByEmailIgnoreCase(user.getEmail());
        } else {
            exists = userRepository.existsByEmailAndUserId(user.getEmail(), user.getEntityId());
        }
        if (exists) {
            throw new ValueAlreadyUsedException("Почта " + user.getEmail() + " уже используется");
        }

        // Подводим итоги валидации
        log.debug("Передано корректное значение почты: {}", user.getEmail());

        // Возвращаем управление
        log.debug("Валидация почты пользователя на уровне сервиса завершена");
    }
}
