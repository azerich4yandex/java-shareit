package ru.practicum.shareit.item.service;

import java.util.Collection;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

public interface ItemService {

    /**
     * Метод возвращает коллекцию вещей, созданных пользователем
     *
     * @param userId идентификатор пользователя
     * @return коллекция {@link ItemDto}
     */
    Collection<ItemDto> findAll(Long userId);

    /**
     * Метод возвращает экземпляр класса {@link ItemDto} по переданному идентификатору
     *
     * @param itemId идентификатор вещи
     * @return экземпляр класса {@link ItemDto}
     */
    ItemDto findById(Long itemId);

    /**
     * Метод проверяет и передаёт для сохранения полученный экземпляр класса {@link NewItemDto} и возвращает его с
     * заполненными полями после сохранения
     *
     * @param userId идентификатор владельца вещи
     * @param dto несохраненный экземпляр класса {@link NewItemDto}
     * @return сохраненный экземпляр класса {@link ItemDto}
     */
    ItemDto create(Long userId, NewItemDto dto);

    /**
     * Метод проверяет и передаёт для обновления полученный экземпляр класса {@link UpdateItemDto} и возвращает его с
     * обновленными полями после обновления
     *
     * @param userId идентификатор владельца вещи
     * @param itemId идентификатор вещи
     * @param dto несохраненный экземпляр класса {@link UpdateItemDto}
     * @return сохраненный экземпляр класса {@link ItemDto}
     */
    ItemDto update(Long userId, Long itemId, UpdateItemDto dto);

    /**
     * Метод проверяет и передает для удаления вещь по её идентификатору
     *
     * @param userId идентификатор владельца
     * @param itemId идентификатор вещи
     */
    void delete(Long userId, Long itemId);
}
