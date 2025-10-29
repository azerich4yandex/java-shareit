package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.model.ItemRequest;

public interface ItemRequestMapper {

    /**
     * Метод преобразует {@link ItemRequest} в {@link ItemRequestFullDto}
     *
     * @param itemRequest экземпляр класса {@link ItemRequest}
     * @return экземпляр класса {@link ItemRequestFullDto}
     */
    ItemRequestFullDto mapToItemRequestFullDto(ItemRequest itemRequest);

    /**
     * Метод преобразует {@link ItemRequest} в {@link ItemRequestShortDto}
     *
     * @param itemRequest экземпляр класса {@link ItemRequest}
     * @return экземпляр класса {@link ItemRequestShortDto}
     */
    ItemRequestShortDto mapToItemRequestShortDto(ItemRequest itemRequest);

    /**
     * Метод преобразует {@link ItemRequestCreateDto} в {@link ItemRequest}
     *
     * @param dto экземпляр класса {@link ItemRequestCreateDto}
     * @return экземпляр класса {@link ItemRequest}
     */
    ItemRequest mapToItemRequest(ItemRequestCreateDto dto);
}
