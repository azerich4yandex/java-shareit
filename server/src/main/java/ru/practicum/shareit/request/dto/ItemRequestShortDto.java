package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;

@Data
@Builder
public class ItemRequestShortDto {

    private Long id;
    private UserDto requestor;
    private String description;
    private LocalDateTime created;
}
