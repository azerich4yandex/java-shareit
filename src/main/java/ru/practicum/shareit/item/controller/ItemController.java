package ru.practicum.shareit.item.controller;

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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

/**
 * Обработка HTTP-запросов для /items
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    /**
     * Обработка GET-запроса к /items
     *
     * @return коллекция {@link ItemDto}
     */
    @GetMapping
    public ResponseEntity<Collection<ItemDto>> findAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Запрос вещей на уровне контроллера");
        log.debug("Запрос от пользователя с id: {}", userId);

        Collection<ItemDto> result = itemService.findAll(userId);
        log.debug("На уровень контроллера вернулась коллекция размером {}", result.size());

        log.debug("Возврат результатов поиска на уровень клиента");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Обработка GET-запроса к /items/search?text={text}
     *
     * @param text поисковая строка
     * @return коллекция {@link ItemDto}
     */
    @GetMapping("/search")
    public ResponseEntity<Collection<ItemDto>> findByText(@RequestParam(name = "text") String text) {
        log.debug("Поиск вещей по вхождению подстроки на уровне контроллера");
        log.debug("Передана поисковая фраза: {}", text);

        Collection<ItemDto> result = itemService.findByText(text);
        log.debug("Возврат результатов поиска по подстроке на уровень клиента");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Обработка GET-запроса к /items/{id}
     *
     * @param itemId идентификатор вещи
     * @return экземпляр класса {@link ItemDto}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> findById(@PathVariable(name = "id") Long itemId) {
        log.debug("Поиск вещи по идентификатору на уровне контроллера");
        log.debug("Передан id вещи: {}", itemId);

        ItemDto result = itemService.findById(itemId);
        log.debug("На уровень контроллера вернулся экземпляр с id {}", result.getId());

        log.debug("Возврат результатов поиска по идентификатору на уровень клиента");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Обработка POST-запроса к /items
     *
     * @param dto несохраненный экземпляр {@link ItemCreateDto}
     * @return сохраненный экземпляр {@link ItemDto}
     */
    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @Valid @RequestBody ItemCreateDto dto) {
        log.debug("Создание вещи на уровне контроллера");
        log.debug("Создание вещи от пользователя с id: {}", userId);

        ItemDto result = itemService.create(userId, dto);
        log.debug("На уровень контроллера после создания вернулся экземпляр с id {}", result.getId());

        log.debug("Возврат результатов создания на уровень клиента");
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    /**
     * Обработка PATCH-запроса к /items/{id}
     *
     * @param itemId идентификатор вещи
     * @param dto обновляемая модель {@link ItemUpdateDto}
     * @return сохраненная модель {@link ItemDto}
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable(name = "id") Long itemId,
                                              @RequestBody ItemUpdateDto dto) {
        log.debug("Обновление вещи на уровне контроллера");
        log.debug("Обновление вещи от пользователя с id: {}", userId);
        log.debug("Передан id обновляемой вещи: {}", itemId);

        ItemDto result = itemService.update(userId, itemId, dto);
        log.debug("На уровень контроллера после обновления вернулся экземпляр с id {}", result.getId());

        log.debug("Возврат результатов изменения на уровень клиента");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Обработка DELETE-запроса к /items/{id}
     *
     * @param itemId идентификатор вещи
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable(name = "id") Long itemId) {
        log.debug("Удаление вещи по идентификатору на уровне контроллера");
        log.debug("Запрос на удаление от пользователя с id: {}", userId);
        log.debug("Передан идентификатор вещи: {}", itemId);

        itemService.delete(userId, itemId);
        log.debug("На уровень контроллера вернулась информация об успешном удалении вещи");

        log.debug("Возврат результатов удаления на уровень клиента");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
