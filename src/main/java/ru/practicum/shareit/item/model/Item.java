package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Вещь.
 */
@Builder
@EqualsAndHashCode(of = "entityId")
@Getter
@Setter
@ToString(of = {"entityId", "sharerId"})
public class Item {

    /**
     * Идентификатор сущности
     */
    private Long entityId;

    /**
     * Идентификатор владельца
     */
    private Long sharerId;

    /**
     * Краткое наименование
     */
    private String name;

    /**
     * Краткое описание
     */
    private String description;

    /**
     * Признак доступности вещи
     */
    private Boolean available;

    public Item() {
    }

    public Item(Long entityId, Long sharerId, String name, String description, Boolean available) {
        this.entityId = entityId;
        this.sharerId = sharerId;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
