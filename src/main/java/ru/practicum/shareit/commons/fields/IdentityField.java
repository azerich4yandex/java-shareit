package ru.practicum.shareit.commons.fields;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Builder
@Data
@EqualsAndHashCode(of = "entityId")
public class IdentityField {

    /**
     * Идентификатор сущности
     */
    protected Long entityId;
}
