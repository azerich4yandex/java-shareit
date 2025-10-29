package ru.practicum.shareit.booking.dto;

public enum BookingState {
    /**
     * Все
     */
    ALL,

    /**
     * Текущие
     */
    CURRENT,

    /**
     * Будущие
     */
    FUTURE,

    /**
     * Прошедшие
     */
    PAST,

    /**
     * Отклоненные
     */
    REJECTED,

    /**
     * Ожидающие подтверждения
     */
    WAITING;

    public static BookingState of(String value) {
        return BookingState.valueOf(value);
    }
}
