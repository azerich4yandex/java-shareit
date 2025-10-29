package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
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
     * Метод преобразует экземпляр класса {@link Item} в {@link ItemShortDto}
     *
     * @param item экземпляр класса {@link Item}
     * @return преобразованный экземпляр класса {@link ItemShortDto}
     */
    ItemShortDto mapToShortDto(Item item);

    /**
     * Метод преобразует модель класса {@link Item} в {@link ItemFullDto}
     *
     * @param item экземпляр класса {@link Item}
     * @return преобразованный экземпляр класса {@link ItemFullDto}
     */
    ItemFullDto mapToFullDto(Item item);

    /**
     * Метод дополняет поля класса {@link Item}, если они заполнены в экземпляре класса {@link ItemUpdateDto}
     *
     * @param dto источник изменений
     * @param item приемник изменений
     */
    void updateItemFields(ItemUpdateDto dto, Item item);
}
