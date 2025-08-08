package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;

public interface BookingMapper {

    /**
     * Метод преобразует модель {@link Booking} в модель {@link BookingFullDto}
     *
     * @param booking модель {@link Booking}
     * @return модель {@link BookingFullDto}
     */
    BookingFullDto mapToFullDto(Booking booking);

    /**
     * Метод преобразует модель {@link Booking} в модель {@link BookingShortDto}
     *
     * @param booking модель {@link Booking}
     * @return модель {@link BookingShortDto}
     */
    BookingShortDto mapToShortDto(Booking booking);

    /**
     * Метод преобразует модель {@link BookingCreateDto} в модель {@link Booking}
     *
     * @param dto модель {@link BookingCreateDto}
     * @return модель {@link Booking}
     */
    Booking mapToBooking(BookingCreateDto dto);
}
