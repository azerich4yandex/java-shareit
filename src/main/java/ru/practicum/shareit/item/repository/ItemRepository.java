package ru.practicum.shareit.item.repository;

import java.util.Collection;
import java.util.Optional;
import ru.practicum.shareit.item.model.Item;

public interface ItemRepository {

    /**
     * Метод возвращает коллекцию экземпляров класса {@link Item} по их владельцу
     *
     * @param userId идентификатор владельца
     * @return коллекция экземпляров класса {@link Item}
     */
    Collection<Item> findAll(Long userId);

    /**
     * Метод возвращает коллекцию экземпляров класса {@link Item} по подстроке
     *
     * @param text поисковая подстрока
     * @return коллекция экземпляров класса {@link Item}
     */
    Collection<Item> findByText(String text);

    /**
     * Метод возвращает экземпляр класса {@link Item}, полученный из хранилища
     *
     * @param itemId идентификатор вещи
     * @return экземпляр класса {@link Item}
     */
    Optional<Item> findById(Long itemId) ;

    /**
     * Метод передает для сохранения в хранилище экземпляр класса {@link Item}
     *
     * @param item несохраненный экземпляр класса {@link Item}
     * @return охраненный экземпляр класса {@link Item}
     */
    Item create(Item item);

    /**
     * Метод передает для обновления в хранилище экземпляр класса {@link Item}
     *
     * @param item экземпляр класса {@link Item} с несохраненными изменениями
     * @return экземпляр класса {@link Item} с сохраненными изменениями
     */
    Item update(Item item);

    /**
     * Метод удаляет из хранилища экземпляр класса {@link Item} по переданному идентификатору
     *
     * @param itemId идентификатор вещи
     */
    void delete(Long itemId);
}
