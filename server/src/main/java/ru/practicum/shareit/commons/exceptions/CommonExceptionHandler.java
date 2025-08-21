package ru.practicum.shareit.commons.exceptions;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.commons.exceptions.dto.ErrorResponse;

@RestControllerAdvice
@Slf4j
public class CommonExceptionHandler {

    /**
     * Обработка исключения {@link ValidationException}
     *
     * @param e обрабатываемое исключение
     * @return сообщение об ошибке и соответствующий HTTP-статус (400 BAD_REQUEST)
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(final ValidationException e) {
        log.warn("Вызвано исключение ValidationException с текстом {}", e.getMessage());

        return new ResponseEntity<>(
                ErrorResponse.builder().error("Ошибка валидации данных").errorMessage(e.getMessage()).build(),
                HttpStatus.BAD_REQUEST);
    }

    /**
     * Обработка исключения {@link UserIsNotSharerException}
     *
     * @param e обрабатываемое исключение
     * @return сообщение об ошибке и соответствующий HTTP-статус (403 FORBIDDEN)
     */
    @ExceptionHandler(UserIsNotSharerException.class)
    public ResponseEntity<ErrorResponse> handleUserIsNotSharerException(final UserIsNotSharerException e) {
        log.warn("Вызвано исключение UserIsNotSharerException с текстом {}", e.getMessage());

        return new ResponseEntity<>(
                ErrorResponse.builder().error("Ошибка доступа").errorMessage(e.getMessage()).build(),
                HttpStatus.FORBIDDEN);
    }

    /**
     * Обработка исключения {@link NotFoundException}
     *
     * @param e обрабатываемое исключение
     * @return сообщение об ошибке и соответствующий HTTP-статус (404 NOT_FOUND)
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(final NotFoundException e) {
        log.warn("Вызвано исключение NotFoundException с текстом {}", e.getMessage());

        return new ResponseEntity<>(
                ErrorResponse.builder().error("Сущность не найдена").errorMessage(e.getMessage()).build(),
                HttpStatus.NOT_FOUND);
    }


    /**
     * Обработка исключения {@link ValueAlreadyUsedException}
     *
     * @param e обрабатываемое исключение
     * @return сообщение об ошибке и соответствующий HTTP-статус (409 CONFLICT)
     */
    @ExceptionHandler(ValueAlreadyUsedException.class)
    public ResponseEntity<ErrorResponse> handleConflict(final ValueAlreadyUsedException e) {
        log.warn("Вызвано исключение ValueAlreadyUsedException с текстом {}", e.getMessage());

        return new ResponseEntity<>(
                ErrorResponse.builder().error("Ошибка уникальности значения").errorMessage(e.getMessage()).build(),
                HttpStatus.CONFLICT);
    }

    /**
     * Обработка исключения {@link RuntimeException}
     *
     * @param e обрабатываемое исключение (500 INTERNAL_SERVER_ERROR)
     * @return сообщение об ошибке и соответствующий HTTP-статус
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(final RuntimeException e) {
        log.warn("Вызвано исключение RuntimeException с текстом {}", e.getMessage());

        return new ResponseEntity<>(
                ErrorResponse.builder().error("Произошла непредвиденная ошибка").errorMessage(e.getMessage()).build(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
