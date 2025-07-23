package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NewItemDto {

    private String name;
    private String description;
    private Boolean available;
}
