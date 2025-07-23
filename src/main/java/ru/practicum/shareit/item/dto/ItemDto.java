package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Builder
@Data
public class ItemDto {

    private Long id;
    private UserDto sharer;
    private String name;
    private String description;
    private Boolean available;
}
