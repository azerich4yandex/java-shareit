package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
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
import ru.practicum.shareit.commons.exceptions.IncorrectDataException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

/**
 * Обработка HTTP-запросов для /items
 */
@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    /**
     * Обработка GET-запроса к /items
     *
     * @return коллекция вещей
     */
    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                          @Positive @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        log.info("Запрос вещей на уровне клиента");

        if (userId == null) {
            throw new IncorrectDataException("Атрибут \"X-Sharer-User-Id\" не найден в заголовке");
        }
        log.info("Запрос от пользователя с id: {}", userId);

        return itemClient.findAll(userId, from, size);
    }

    /**
     * Обработка GET-запроса к /items/search?text={text}
     *
     * @param text поисковая строка
     * @return коллекция вещей
     */
    @GetMapping("/search")
    public ResponseEntity<Object> findByText(@RequestParam(name = "text") String text,
                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                             @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Поиск вещей по вхождению подстроки на уровне клиента");

        if (text == null || text.trim().isBlank()) {
            log.debug("Передано пустое значение подстроки. Возвращаем пустую коллекцию на уровень контроллера");
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }
        log.info("Передана поисковая фраза: {}", text);

        return itemClient.findByText(text, from, size);
    }

    /**
     * Обработка GET-запроса к /items/{id}
     *
     * @param itemId идентификатор вещи
     * @return экземпляр класса вещи
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                           @PathVariable(name = "id") Long itemId) {
        log.info("Поиск вещи по идентификатору на уровне клиента");

        if (ownerId == null) {
            throw new IncorrectDataException("Идентификатор владельца должен быть указан");
        }
        log.info("Передан идентификатор владельца: {}", ownerId);

        if (itemId == null) {
            throw new IncorrectDataException("Идентификатор вещи должен быть указан");
        }
        log.info("Передан id вещи: {}", itemId);

        return itemClient.findById(itemId, ownerId);
    }

    /**
     * Обработка POST-запроса к /items
     *
     * @param dto несохраненный экземпляр {@link ItemCreateDto}
     * @return сохраненный экземпляр вещи
     */
    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody ItemCreateDto dto) {
        log.info("Создание вещи на уровне клиента");
        log.debug("Передана модель DTO для создания вещи: {}", dto);

        if (userId == null) {
            throw new IncorrectDataException("Атрибут \"X-Sharer-User-Id\" не найден в заголовке");
        }
        log.info("Запрос от владельца с id: {}", userId);

        return itemClient.addItem(userId, dto);
    }

    /**
     * Обработка POST-запроса к /items/{itemId}/comment
     *
     * @param authorId идентификатор автора комментария
     * @param itemId идентификатор комментируемой вещи
     * @param dto несохранённый экземпляр {@link CommentCreateDto}
     */
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Long authorId,
                                                @PathVariable(name = "itemId") Long itemId,
                                                @Valid @RequestBody CommentCreateDto dto) {
        log.info("Создание комментария на уровне клиента");
        log.debug("Передана модель DTO для создания комментария: {}", dto);

        if (authorId == null) {
            throw new IncorrectDataException("Атрибут \"X-Sharer-User-Id\" не найден в заголовке");
        }
        log.info("Передан идентификатор автора комментария: {}", authorId);

        if (itemId == null) {
            throw new IncorrectDataException("Идентификатор вещи должен быть указан");
        }
        log.info("Передан идентификатор комментируемой вещи: {}", itemId);

        return itemClient.addComment(authorId, itemId, dto);
    }

    /**
     * Обработка PATCH-запроса к /items/{id}
     *
     * @param itemId идентификатор вещи
     * @param dto обновляемая модель {@link ItemUpdateDto}
     * @return сохраненная модель вещи
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable(name = "id") Long itemId,
                                             @RequestBody ItemUpdateDto dto) {
        log.info("Обновление вещи на уровне клиента");
        log.debug("Передана модель DTO для обновления вещи: {}", dto);

        if (userId == null) {
            throw new IncorrectDataException("Атрибут \"X-Sharer-User-Id\" не найден в заголовке");
        }
        log.info("Обновление вещи от пользователя с id: {}", userId);

        if (itemId == null) {
            throw new IncorrectDataException("Идентификатор вещи должен быть указан");
        }
        log.info("Передан id обновляемой вещи: {}", itemId);

        return itemClient.updateItem(userId, itemId, dto);
    }

    /**
     * Обработка DELETE-запроса к /items/{id}
     *
     * @param itemId идентификатор вещи
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable(name = "id") Long itemId) {
        log.info("Удаление вещи по идентификатору на уровне клиента");

        if (userId == null) {
            throw new IncorrectDataException("Атрибут \"X-Sharer-User-Id\" не найден в заголовке");
        }
        log.info("Запрос на удаление от пользователя с id: {}", userId);

        if (itemId == null) {
            throw new IncorrectDataException("Идентификатор вещи должен быть указан");
        }
        log.info("Передан идентификатор вещи: {}", itemId);

        return itemClient.deleteItem(userId, itemId);
    }
}
