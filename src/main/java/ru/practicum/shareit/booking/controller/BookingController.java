package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

/**
 * Обработка HTTP-запросов к /bookings
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    /**
     * Обработка GET-запроса к /bookings?state={state}
     *
     * @param bookerId идентификатор бронирующего
     * @param state состояние бронирования
     * @return коллекция {@link BookingDto}
     */
    @GetMapping
    public ResponseEntity<Collection<BookingDto>> getByBooker(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                              @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        log.debug("Запрос бронирований, созданных пользователем на уровне контроллера");
        log.debug("Передан идентификатор бронирующего: {}", bookerId);
        log.debug("Передано состояние бронирования: {}", state);

        Collection<BookingDto> result = bookingService.findAllByBookerAndState(bookerId, state);
        log.debug("На уровень контроллера вернулась коллекция бронирований пользователя размером {}", result.size());

        log.debug("Возврат результатов поиска бронирований на уровень клиента");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Обработка GET-запроса к /bookings/owner?state={state}
     *
     * @param ownerId идентификатор владельца
     * @param state состояние бронирования
     * @return коллекция {@link BookingDto}
     */
    @GetMapping("/owner")
    public ResponseEntity<Collection<BookingDto>> getByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                             @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        log.debug("Запрос бронирований по владельцу вещей на уровне контроллера");
        log.debug("Передан идентификатор владельца вещей: {}", ownerId);
        log.debug("Передано состояние бронирования вещей: {}", state);

        Collection<BookingDto> result = bookingService.findAllByOwnerAndState(ownerId, state);
        log.debug("На уровень контроллера вернулась коллекция бронирований вещей владельца размером {}", result.size());

        log.debug("Возврат результатов бронирования на уровень клиента");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getById(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                              @PathVariable(name = "bookingId") Long bookingId) {
        log.debug("Запрос бронирования по идентификатору на уровне контроллера");
        log.debug("Передан идентификатор пользователя: {}", bookerId);
        log.debug("Передан идентификатор бронирования: {}", bookingId);

        BookingDto result = bookingService.findByBookerIdAndBookingId(bookerId, bookingId);
        log.debug("На уровень контроллера вернулся экземпляр бронирования с id {}", result.getId());

        log.debug("Возврат результата поиск на уровень клиента");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Обработка POST-запроса к /bookings
     *
     * @param bookerId идентификатор бронирующего
     * @param dto несохранённый экземпляр {@link BookingCreateDto}
     * @return сохраненная модель
     */
    @PostMapping
    public ResponseEntity<BookingDto> create(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                             @Valid @RequestBody BookingCreateDto dto) {
        log.debug("Создание бронирования на уровне контроллера");
        log.debug("Передан идентификатор бронирующего пользователя: {}", bookerId);

        BookingDto result = bookingService.create(bookerId, dto);
        log.debug("На уровень контроллера после создания вернулось бронирование с id {}", result.getId());

        log.debug("Возврат результатов создания на уровень клиента");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Обработка PATCH-запроса к /bookings/{bookingId}?approved={approved}
     *
     * @param ownerId идентификатор владельца вещи
     * @param bookingId идентификатор бронирования
     * @param approved статус согласования бронирования
     * @return модель {@link BookingDto}
     */
    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approve(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                              @PathVariable(name = "bookingId") Long bookingId,
                                              @RequestParam(name = "approved") Boolean approved) {
        log.debug("Изменение согласования бронирования на уровне контроллера");
        log.debug("Передан идентификатор владельца вещи: {}", ownerId);
        log.debug("Передан идентификатор изменяемого бронирования: {}", bookingId);
        log.debug("Передан статус согласования бронирования: {}", approved);

        BookingDto result = bookingService.approve(ownerId, bookingId, approved);
        log.debug("На уровень контроллера вернулось измененное бронирование с id {}", result.getId());

        log.debug("Возврат результатов изменения бронирования на уровень клиента");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
