package ru.practicum.shareit.item.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentShortDto {

    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
