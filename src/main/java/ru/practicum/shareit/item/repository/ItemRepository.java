package ru.practicum.shareit.item.repository;

import java.util.Collection;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * Метод возвращает коллекцию вещей по идентификатору владельца
     *
     * @param sharerId идентификатор владельца
     * @return коллекция {@link Item}
     */
    Collection<Item> findAllBySharerEntityId(Long sharerId, Sort sort);

    /**
     * Метод возвращает коллекцию доступных к бронированию вещей, в названии которых встречается переданная подстрока
     *
     * @param searchText поисковая подстрока
     * @param available признак доступности бронирования
     * @return коллекция {@link Item}
     */
    @Query("SELECT i "
            + "FROM Item AS i "
            + "WHERE UPPER(i.name) LIKE CONCAT('%', :searchText, '%') "
            + "AND i.available = :available")
    Collection<Item> findAllByNameAndAvailable(String searchText, Boolean available, Sort sort);
}
