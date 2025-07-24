package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NewItemDto {

    @NotBlank(message = "Наименование вещи должно быть указано")
    private String name;

    @NotBlank(message = "Описание вещи должно быть указано")
    private String description;

    @NotNull(message = "Признак доступности должен быть указан")
    private Boolean available;
}
