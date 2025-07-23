package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.commons.fields.IdentityField;

/**
 * Вещь.
 */
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
public class Item extends IdentityField {

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
