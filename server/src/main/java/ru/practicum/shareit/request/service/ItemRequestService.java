package ru.practicum.shareit.request.service;

import java.util.Collection;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;

public interface ItemRequestService {

    /**
     * Метод возвращает коллекцию всех {@link ItemRequestShortDto}, созданных другими пользователями
     *
     * @param from номер начального элемента коллекции
     * @param size максимальный размер коллекции
     * @return коллекция {@link ItemRequestShortDto}
     */
    Collection<ItemRequestShortDto> findAll(Integer from, Integer size);

    /**
     * Метод возвращает коллекцию {@link ItemRequestFullDto}, созданных пользователем
     *
     * @param requestorId идентификатор пользователя
     * @return коллекция {@link ItemRequestFullDto}
     */
    Collection<ItemRequestFullDto> findByRequestorId(Long requestorId, Integer from, Integer size);

    /**
     * Метод возвращает экземпляр {@link ItemRequestFullDto} по переданному идентификатору
     *
     * @param itemRequestId идентификатор запроса
     * @return экземпляр {@link ItemRequestFullDto}
     */
    ItemRequestFullDto findById(Long itemRequestId);

    /**
     * Метод проверяет и передаёт для сохранения полученный экземпляр класса {@link ItemRequestCreateDto} и возвращает
     * его с заполненными после сохранения полями
     *
     * @param requestorId идентификатор автора запроса
     * @param dto несохранённый экземпляр класса {@link ItemRequestCreateDto}
     * @return сохраненный экземпляр класса {@link ItemRequestFullDto}
     */
    ItemRequestFullDto create(Long requestorId, ItemRequestCreateDto dto);
}
