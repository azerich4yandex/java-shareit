package ru.practicum.shareit.item.service;

import java.util.Collection;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

public interface ItemService {

    /**
     * Метод возвращает коллекцию вещей, созданных пользователем
     *
     * @param userId идентификатор пользователя
     * @return коллекция {@link ItemShortDto}
     */
    Collection<ItemFullDto> findAllByOwner(Long userId);

    /**
     * Метод возвращает коллекцию вещей, в текстовых полях которых встречается переданная подстрока
     *
     * @param text поисковая подстрока
     * @return коллекция {@link ItemShortDto}
     */
    Collection<ItemShortDto> findByText(String text);

    /**
     * Метод возвращает экземпляр класса {@link ItemShortDto} по переданному идентификатору
     *
     * @param itemId идентификатор вещи
     * @return экземпляр класса {@link ItemShortDto}
     */
    ItemFullDto findById(Long itemId);

    /**
     * Метод проверяет и передаёт для сохранения полученный экземпляр класса {@link ItemCreateDto} и возвращает его с
     * заполненными полями после сохранения
     *
     * @param userId идентификатор владельца вещи
     * @param dto несохраненный экземпляр класса {@link ItemCreateDto}
     * @return сохраненный экземпляр класса {@link ItemShortDto}
     */
    ItemShortDto create(Long userId, ItemCreateDto dto);

    /**
     * Метод проверяет и передаёт для обновления полученный экземпляр класса {@link ItemUpdateDto} и возвращает его с
     * обновленными полями после обновления
     *
     * @param userId идентификатор владельца вещи
     * @param itemId идентификатор вещи
     * @param dto несохраненный экземпляр класса {@link ItemUpdateDto}
     * @return сохраненный экземпляр класса {@link ItemShortDto}
     */
    ItemShortDto update(Long userId, Long itemId, ItemUpdateDto dto);

    /**
     * Метод проверяет и передает для удаления вещь по её идентификатору
     *
     * @param userId идентификатор владельца
     * @param itemId идентификатор вещи
     */
    void delete(Long userId, Long itemId);
}
