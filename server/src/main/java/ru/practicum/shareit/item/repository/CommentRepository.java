package ru.practicum.shareit.item.repository;

import java.util.Collection;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Метод возвращает коллекцию {@link Comment}, связанных с {@link Item}
     *
     * @param itemId идентификатор вещи
     * @param sort порядок сортировки
     * @return коллекция {@link Comment}
     */
    Collection<Comment> findAllByItemEntityId(Long itemId, Sort sort);
}
