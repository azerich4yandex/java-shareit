package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;
import java.util.Collection;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserDto;

@Data
@Builder
public class ItemRequestFullDto {

    private Long id;
    private String description;
    private UserDto requestor;
    private LocalDateTime created;
    private Collection<ItemShortDto> items;
}
