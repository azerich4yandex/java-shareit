package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.commons.exceptions.IncorrectDataException;


/**
 * Обработка HTTP-запросов к /bookings
 */
@RequiredArgsConstructor
@Slf4j
@Controller
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingClient bookingClient;

    /**
     * Обработка GET-запроса к /bookings?state={state}
     *
     * @param userId идентификатор бронирующего
     * @param stateParam состояние бронирования
     * @param from номер начального элемента коллекции
     * @param size максимальный размер возвращаемой коллекции
     * @return коллекция бронирований
     */
    @GetMapping
    public ResponseEntity<Object> getBookingsByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                      @Positive @RequestParam(name = "size", defaultValue = "100") Integer size) {
        log.info("Запрос бронирований, созданных пользователем на уровне клиента");

        if (userId == null) {
            throw new IncorrectDataException("Атрибут \"X-Sharer-User-Id\" не найден в заголовке");
        }
        log.info("Передан идентификатор бронирующего: {}", userId);

        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Неизвестное значение: " + stateParam));
        log.info("Передано состояние бронирования: {}", state);

        return bookingClient.getBookingsByBooker(userId, state, from, size);
    }

    /**
     * Обработка GET-запроса к /bookings/owner?state={state}
     *
     * @param ownerId идентификатор владельца
     * @param stateParam состояние бронирования
     * @param from номер начального элемента коллекции
     * @param size максимальный размер возвращаемой коллекции
     * @return коллекция бронирований
     */
    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                    @RequestParam(name = "state", required = false, defaultValue = "ALL") String stateParam,
                                                    @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                    @Positive @RequestParam(name = "size", defaultValue = "100") Integer size) {
        log.info("Запрос бронирований по владельцу вещей на уровне клиента");

        if (ownerId == null) {
            throw new IncorrectDataException("Атрибут \"X-Sharer-User-Id\" не найден в заголовке");
        }
        log.info("Передан идентификатор владельца вещей: {}", ownerId);

        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Неизвестное значение: " + stateParam));
        log.info("Передано состояние бронирования вещей: {}", state);

        return bookingClient.getBookingsByOwner(ownerId, state, from, size);
    }

    /**
     * Обработка GET-запроса к /bookings/{bookingId}
     *
     * @param userId идентификатор бронирующего
     * @param bookingId идентификатор брони
     * @return экземпляр бронирования
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable(name = "bookingId") Long bookingId) {
        log.info("Запрос бронирования по идентификатору на уровне клиента");

        if (userId == null) {
            throw new IncorrectDataException("Атрибут \"X-Sharer-User-Id\" не найден в заголовке");
        }
        log.info("Передан идентификатор пользователя: {}", userId);

        if (bookingId == null) {
            throw new IncorrectDataException("Идентификатор бронирования должен быть указан");
        }
        log.info("Передан идентификатор бронирования: {}", bookingId);

        return bookingClient.getBooking(userId, bookingId);
    }

    /**
     * Обработка POST-запроса к /bookings
     *
     * @param userId идентификатор бронирующего
     * @param requestDto несохранённый экземпляр {@link BookItemRequestDto}
     * @return сохраненная модель
     */
    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Создание бронирования на уровне клиента");
        log.debug("Передана модель DTO: {}", requestDto);

        if (userId == null) {
            throw new IncorrectDataException("Атрибут \"X-Sharer-User-Id\" не найден в заголовке");
        }

        if (!requestDto.getEnd().isAfter(requestDto.getStart())) {
            throw new IncorrectDataException(
                    "Дата окончания бронирования не может быть меньше даты начал бронирования");
        } else if (requestDto.getStart().equals(requestDto.getEnd())) {
            throw new IncorrectDataException(
                    "Дата начала бронирования и дата окончания бронирования не могут быть равны");
        }

        return bookingClient.createBooking(userId, requestDto);
    }

    /**
     * Обработка PATCH-запроса к /bookings/{bookingId}?approved={approved}
     *
     * @param ownerId идентификатор владельца вещи
     * @param bookingId идентификатор бронирования
     * @param approved статус согласования бронирования
     * @return обновленный экземпляр бронирования
     */
    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                 @PathVariable(name = "bookingId") Long bookingId,
                                                 @RequestParam(name = "approved") Boolean approved) {
        log.info("Изменение согласования бронирования на уровне клиента");

        if (ownerId == null) {
            throw new IncorrectDataException("Атрибут \"X-Sharer-User-Id\" не найден в заголовке");
        }
        log.info("Передан идентификатор владельца вещи: {}", ownerId);

        if (bookingId == null) {
            throw new IncorrectDataException("Идентификатор брони должен быть указан");
        }
        log.info("Передан идентификатор изменяемого бронирования: {}", bookingId);

        if (approved == null) {
            throw new IncorrectDataException("Статус согласования бронирования должен быть указан");
        }
        log.info("Передан статус согласования бронирования: {}", approved);

        return bookingClient.approveBooking(ownerId, bookingId, approved);
    }
}