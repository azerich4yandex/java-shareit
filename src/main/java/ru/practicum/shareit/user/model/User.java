package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.commons.fields.IdentityField;

/**
 * Пользователь.
 */
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends IdentityField {

    /**
     * Имя пользователя
     */
    private String name;

    /**
     * Почта пользователя
     */
    private String email;
}
