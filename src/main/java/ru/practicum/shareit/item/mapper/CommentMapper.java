package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentShortDto;
import ru.practicum.shareit.item.model.Comment;

public interface CommentMapper {


    /**
     * Метод преобразует {@link CommentCreateDto} в {@link Comment}
     * @param dto экземпляр класса {@link CommentCreateDto}
     * @return экземпляр класса {@link Comment}
     */
    Comment mapToComment(CommentCreateDto dto);

    /**
     * Метод преобразует {@link Comment} в {@link CommentShortDto}
     * @param comment экземпляр класса {@link Comment}
     * @return экземпляр класса {@link CommentShortDto}
     */
    CommentShortDto mapToShortDto(Comment comment);
}
