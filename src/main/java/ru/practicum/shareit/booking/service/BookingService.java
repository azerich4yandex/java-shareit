package ru.practicum.shareit.booking.service;

import jakarta.validation.Valid;
import java.util.Collection;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

public interface BookingService {

    /**
     * Метод возвращает коллекцию {@link BookingDto} с учетом идентификатора бронирующего и статуса бронирования
     *
     * @param bookerId идентификатор бронирующего
     * @param state статус бронирования
     * @return коллекция {@link BookingDto}
     */
    Collection<BookingDto> findAllByBookerAndState(Long bookerId, String state);

    /**
     * Метод возвращает коллекцию {@link BookingDto} с учетом идентификатор собственника и статуса бронирования
     *
     * @param ownerId идентификатор собственника
     * @param state статус бронирования
     * @return коллекция {@link BookingDto}
     */
    Collection<BookingDto> findAllByOwnerAndState(Long ownerId, String state);

    /**
     * Метод возвращает экземпляр {@link BookingDto} с учетом идентификатора бронирующего и идентификатора бронирования
     *
     * @param bookerId идентификатор бронирующего
     * @param bookingId идентификатор бронирования
     * @return экземпляр {@link BookingDto}
     */
    BookingDto findByBookerIdAndBookingId(Long bookerId, Long bookingId);

    /**
     * Метод передаёт для сохранения модель {@link BookingCreateDto} и идентификатор бронирующего
     *
     * @param bookerId идентификатор бронирующего
     * @param dto несохраненная модель {@link BookingCreateDto}
     * @return сохраненная модель {@link BookingDto}
     */
    BookingDto create(Long bookerId, @Valid BookingCreateDto dto);

    /**
     * Метод изменяет статус бронирования с учетом переданного идентификатора владельца вещи, идентификатора
     * бронирования и статуса согласования
     *
     * @param ownerId идентификатор владельца вещи
     * @param bookingId идентификатор брони
     * @param approved статус согласования бронирования
     * @return измененная модель {@link BookingDto}
     */
    BookingDto approve(Long ownerId, Long bookingId, Boolean approved);
}
