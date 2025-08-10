package ru.practicum.shareit.booking.dto;

import jakarta.validation.ValidationException;

public enum BookingState {
    ALL,
    CURRENT,
    FUTURE,
    PAST,
    REJECTED,
    WAITING;

    public static BookingState validate(String value) {
        try {
            return BookingState.valueOf(value);
        } catch (Exception e) {
            throw new ValidationException("Неизвестный тип статуса бронирования: " + value);
        }
    }
}
