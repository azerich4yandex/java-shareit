package ru.practicum.shareit.commons.exceptions;

import jakarta.validation.ValidationException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException.Conflict;

@RestControllerAdvice
@Slf4j
public class CommonExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(final NotFoundException e) {
        log.warn("Вызвано исключение NotFoundException с текстом {}", e.getMessage());

        return new ResponseEntity<>(Map.of("error", "Сущность не найдена", "errorMessage", e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidation(final ValidationException e) {
        log.warn("Вызвано исключение ValidationException с текстом {}", e.getMessage());

        return new ResponseEntity<>(Map.of("error", "Ошибка валидации данных", "errorMessage", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(final RuntimeException e) {
        log.warn("Вызвано исключение RuntimeException с текстом {}", e.getMessage());

        return new ResponseEntity<>(Map.of("error", "Произошла непредвиденная ошибка", "errorMessage", e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ValueAlreadyUsedException.class)
    public ResponseEntity<Map<String, String>> handleConflict(final ValueAlreadyUsedException e) {
        log.warn("Вызвано исключение ValueAlreadyUsedException с текстом {}", e.getMessage());

        return new ResponseEntity<>(Map.of("error", "Ошибка уникальности значения", "errorMessage", e.getMessage()),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserIsNotSharerException.class)
    public ResponseEntity<Map<String, String>> handleUserIsNotSharerException(final UserIsNotSharerException e) {
        log.warn("Вызвано исключение UserIsNotSharerException с текстом {}", e.getMessage());

        return new ResponseEntity<>(Map.of("error", "Ошибка доступа", "errorMessage", e.getMessage()),
                HttpStatus.FORBIDDEN);
    }
}
