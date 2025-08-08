package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

public interface BookingMapper {

    /**
     * Метод преобразует модель {@link Booking} в модель {@link BookingDto}
     *
     * @param booking модель {@link Booking}
     * @return модель {@link BookingDto}
     */
    BookingDto mapToBookingDto(Booking booking);

    /**
     * Метод преобразует модель {@link BookingCreateDto} в модель {@link Booking}
     *
     * @param dto модель {@link BookingCreateDto}
     * @return модель {@link Booking}
     */
    Booking mapToBooking(BookingCreateDto dto);
}
