package ru.practicum.shareit.request.controller;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.service.ItemRequestService;

/**
 * Обработка HTTP-запросов для /requests
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    /**
     * Обработка GET-запрос к /requests/all
     *
     * @return коллекция {@link ItemRequestFullDto}
     */
    @GetMapping("/all")
    public ResponseEntity<Collection<ItemRequestShortDto>> findAll(
            @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        log.debug("Получение всех запросов, созданных другими пользователями на уровне контроллера");

        Collection<ItemRequestShortDto> result = itemRequestService.findAll(from, size);
        log.debug("На уровень контроллера вернулась коллекция всех запросов размером {}", result.size());

        log.debug("Возврат результатов на уровень контроллера");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Обработка GET-запроса к /requests
     *
     * @param requestorId идентификатор автора запросов
     * @return коллекция {@link ItemRequestFullDto}
     */
    @GetMapping
    public ResponseEntity<Collection<ItemRequestFullDto>> findByRequestorId(
            @RequestHeader("X-Sharer-User-Id") Long requestorId,
            @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        log.debug("Получение всех своих запросов на уровне контроллера");
        log.debug("Запрос поступил от пользователя с идентификатором: {}", requestorId);

        Collection<ItemRequestFullDto> result = itemRequestService.findByRequestorId(requestorId, from, size);
        log.debug("На уровень контроллера вернулась коллекция собственных запросов размером {}", result.size());

        log.debug("Возврат результатов поиска на уровень клиента");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Обработка GET-запроса к /requests/{requestId}
     *
     * @param itemRequestId идентификатор запроса
     * @return экземпляр {@link ItemRequestFullDto}
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestFullDto> findById(@PathVariable(name = "requestId") Long itemRequestId) {
        log.debug("Получение запроса по его идентификатору на уровне контроллера");
        log.debug("Передан идентификатор запроса: {}", itemRequestId);

        ItemRequestFullDto result = itemRequestService.findById(itemRequestId);
        log.debug("На уровень контроллера вернулся запрос с id {}", result.getId());

        log.debug("Возврат результатов поиска по идентификатору на уровень клиента");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Обработка POST-запроса к /requests
     *
     * @param requestorId идентификатор автора запроса
     * @param dto несохраненный экземпляр {@link ItemRequestCreateDto}
     * @return сохраненный экземпляр {@link ItemRequestFullDto}
     */
    @PostMapping
    public ResponseEntity<ItemRequestFullDto> createItemRequest(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                                                                @RequestBody ItemRequestCreateDto dto) {
        log.debug("Создание запрос на уровне контроллера");
        log.debug("Идентификатор автора запроса: {}", requestorId);

        ItemRequestFullDto result = itemRequestService.create(requestorId, dto);
        log.debug("На уровень контроллера после создания вернулся запрос с id {}", result.getId());

        log.debug("Возврат результатов создания на уровень контроллера");
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
}
