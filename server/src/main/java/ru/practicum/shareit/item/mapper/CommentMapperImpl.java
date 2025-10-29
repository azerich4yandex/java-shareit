package ru.practicum.shareit.item.mapper;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentShortDto;
import ru.practicum.shareit.item.model.Comment;

@Service
@Slf4j
public class CommentMapperImpl implements CommentMapper {

    @Override
    public Comment mapToComment(CommentCreateDto dto) {
        log.debug("Преобразование данных из модели {} в модель {} для сохранения", CommentCreateDto.class,
                Comment.class);
        return Comment.builder()
                .text(dto.getText())
                .created(LocalDateTime.now())
                .build();
    }

    @Override
    public CommentShortDto mapToShortDto(Comment comment) {
        log.debug("Преобразование данных из модели {} в краткую модель {}", Comment.class, CommentShortDto.class);
        return CommentShortDto.builder()
                .id(comment.getEntityId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }
}
