package ru.practicum.shareit.item.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateDto {

    private String text;
    private Long authorId;
    private LocalDateTime created;
}
