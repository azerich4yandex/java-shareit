package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

/**
 * Обработка HTTP-запросов для /requests
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    /**
     * Обработка GET-запрос к /requests/all
     *
     * @return коллекция запросов
     */
    @GetMapping("/all")
    public ResponseEntity<Object> findAll(
            @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        log.info("Получение всех запросов, созданных другими пользователями на уровне клиента");

        return itemRequestClient.findAll(from, size);
    }

    /**
     * Обработка GET-запроса к /requests
     *
     * @param requestorId идентификатор автора запросов
     * @return коллекция запросов
     */
    @GetMapping
    public ResponseEntity<Object> findByRequestorId(
            @RequestHeader("X-Sharer-User-Id") Long requestorId,
            @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        log.info("Получение всех своих запросов на уровне клиента");

        if (requestorId == null) {
            throw new ValidationException("Атрибут \"X-Sharer-User-Id\" не найден в заголовке");
        }
        log.info("Запрос поступил от пользователя с идентификатором: {}", requestorId);

        return itemRequestClient.getByRequestorId(requestorId, from, size);
    }

    /**
     * Обработка GET-запроса к /requests/{requestId}
     *
     * @param itemRequestId идентификатор запроса
     * @return экземпляр запроса
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@PathVariable(name = "requestId") Long itemRequestId) {
        log.info("Получение запроса по его идентификатору на уровне клиента");

        if (itemRequestId == null) {
            throw new ValidationException("Идентификатор запроса должен быть указан");
        }
        log.info("Передан идентификатор запроса: {}", itemRequestId);

        return itemRequestClient.getById(itemRequestId);
    }

    /**
     * Обработка POST-запроса к /requests
     *
     * @param requestorId идентификатор автора запроса
     * @param dto несохраненный экземпляр {@link ItemRequestCreateDto}
     * @return сохраненный экземпляр запроса
     */
    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                                                    @Valid @RequestBody ItemRequestCreateDto dto) {
        log.info("Создание запрос на уровне клиента");
        log.debug("Передана модель DTO для создания запроса: {}",dto);

        if (requestorId == null) {
            throw new ValidationException("Атрибут \"X-Sharer-User-Id\" не найден в заголовке");
        }
        log.info("Идентификатор автора запроса: {}", requestorId);

        return itemRequestClient.createItemRequest(requestorId, dto);
    }
}
