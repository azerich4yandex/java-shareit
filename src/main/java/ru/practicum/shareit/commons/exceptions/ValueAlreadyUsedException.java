package ru.practicum.shareit.commons.exceptions;

public class ValueAlreadyUsedException extends RuntimeException {

    public ValueAlreadyUsedException(String message) {
        super(message);
    }
}
