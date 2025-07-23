package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

/**
 * Вещь.
 */
@Builder
@Data
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
}
