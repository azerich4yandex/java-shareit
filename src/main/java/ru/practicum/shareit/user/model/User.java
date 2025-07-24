package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Пользователь.
 */
@Builder
@EqualsAndHashCode(of = "entityId")
@Getter
@Setter
@ToString(of = {"entityId", "email"})
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

    public User() {
    }

    public User(Long entityId, String name, String email) {
        this.entityId = entityId;
        this.name = name;
        this.email = email;
    }
}
