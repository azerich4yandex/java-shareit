package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemCreateDto {

    @NotBlank(message = "Наименование вещи должно быть указано")
    private String name;

    @NotBlank(message = "Описание вещи должно быть указано")
    private String description;

    @NotNull(message = "Признак доступности должен быть указан")
    private Boolean available;

    private Long requestId;
}
