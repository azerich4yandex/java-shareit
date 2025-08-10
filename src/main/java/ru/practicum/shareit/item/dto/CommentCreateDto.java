package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentCreateDto {

    @NotBlank(message = "Сообщение не может быть пустым")
    private String text;

    private Long authorId;
    private LocalDateTime created;
}
