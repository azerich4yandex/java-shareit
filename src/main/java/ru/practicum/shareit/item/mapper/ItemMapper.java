package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

public interface ItemMapper {

    /**
     * Метод преобразует {@link ItemCreateDto} в {@link Item}
     *
     * @param dto экземпляр класса {@link ItemCreateDto}
     * @return преобразованный экземпляр класса {@link Item}
     */
    Item mapToItem(ItemCreateDto dto);

    /**
     * Метод преобразует экземпляр класса {@link Item} в {@link ItemDto}
     *
     * @param item экземпляр класса {@link Item}
     * @return преобразованный экземпляр класса {@link ItemDto}
     */
    ItemDto mapToItemDto(Item item);

    /**
     * Метод дополняет поля класса {@link Item}, если они заполнены в экземпляре класса {@link ItemUpdateDto}
     *
     * @param dto источник изменений
     * @param item приемник изменений
     */
    void updateItemFields(ItemUpdateDto dto, Item item);
}
