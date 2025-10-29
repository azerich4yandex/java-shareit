package ru.practicum.shareit.item.repository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * Метод возвращает коллекцию вещей по идентификатору владельца
     *
     * @param sharerId идентификатор владельца
     * @param pageable ограничения выборки и порядок сортировки
     * @return коллекция {@link Item}
     */
    Page<Item> findAllBySharerEntityId(Long sharerId, Pageable pageable);

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
    Page<Item> findAllByNameAndAvailable(String searchText, Boolean available, Pageable pageable);


    /**
     * Метод возвращает коллекцию вещей, которые связаны с коллекцией идентификаторов запросов
     *
     * @param requestIds коллекция идентификаторов запросов
     * @return коллекция {@link Item}
     */
    Collection<Item> findByRequestEntityIdIn(List<Long> requestIds, Sort sort);
}
