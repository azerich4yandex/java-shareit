package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;

@Builder
@Data
public class ItemShortDto {

    private Long id;
    private UserDto sharer;
    private String name;
    private String description;
    private Boolean available;
}
