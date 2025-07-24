package ru.practicum.shareit.commons.exceptions.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Сообщение об ошибке.
 */
@Builder
@Data
public class ErrorResponse {

    /**
     * Название ошибки
     */
    private String error;

    /**
     * Текст ошибки
     */
    private String errorMessage;
}
