package ru.practicum.shareit.item.repository;

import java.util.Collection;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Collection<Comment> findAllByItemEntityId(Long itemId, Sort sort);
}
