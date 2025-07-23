package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

/**
 * Пользователь.
 */
@Builder
@Data
public class User {

    /**
     * Идентификатор сущности
     */
    private Long entityId;

    /**
     * Имя пользователя
     */
    private String name;

    /**
     * Почта пользователя
     */
    private String email;
}
