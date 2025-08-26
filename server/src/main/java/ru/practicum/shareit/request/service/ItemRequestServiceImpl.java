package ru.practicum.shareit.request.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.commons.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@RequiredArgsConstructor
@Service
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private static final Sort SORT_CREATED_DESC = Sort.by(Direction.DESC, "created");

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;


    @Override
    public Collection<ItemRequestShortDto> findAll(Integer from, Integer size) {
        log.debug("Получение коллекции всех запросов на уровне контроллера");

        PageRequest pageRequest = PageRequest.of(from, size, SORT_CREATED_DESC);

        Page<ItemRequest> searchResult = itemRequestRepository.findAll(pageRequest);

        log.debug("На уровень сервиса поступила коллекция запросов размером {}", searchResult.getContent().size());

        Collection<ItemRequestShortDto> result = searchResult.stream()
                .map(itemRequestMapper::mapToItemRequestShortDto)
                .toList();
        log.debug("Полученная коллекция всех запросов преобразована");

        log.debug("Возврат коллекции всех запросов на уровень контроллера ");
        return result;
    }

    @Override
    public Collection<ItemRequestFullDto> findByRequestorId(Long requestorId, Integer from, Integer size) {
        log.debug("Получение всех запросов, созданных пользователем на уровне сервиса");

        User requestor = userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + requestorId + " не найден"));
        log.debug("Получен идентификатор автора запросов: {}", requestor.getEntityId());

        PageRequest pageRequest = PageRequest.of(from, size, SORT_CREATED_DESC);

        Collection<ItemRequest> searchResult = itemRequestRepository.findByRequestorEntityId(requestor.getEntityId(),
                pageRequest).getContent();
        log.debug("На уровень сервиса вернулась коллекция запросов, созданных пользователем размером {}",
                searchResult.size());

        Collection<ItemRequestFullDto> result = searchResult.stream()
                .map(itemRequestMapper::mapToItemRequestFullDto)
                .toList();
        completeModel(result);
        log.debug("Полученная коллекция преобразована");

        log.debug("Возврат коллекции всех созданных запросов на уровень контроллера");
        return result;
    }

    @Override
    public ItemRequestFullDto findById(Long itemRequestId) {
        log.debug("Поиск запроса по идентификатору");
        log.debug("Передан идентификатор запроса: {}", itemRequestId);

        ItemRequest searchResult = itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id " + itemRequestId + " не найден"));
        log.debug("На уровень сервиса вернулся запрос с id {}", searchResult.getEntityId());

        ItemRequestFullDto result = itemRequestMapper.mapToItemRequestFullDto(searchResult);

        Collection<Item> items = itemRepository.findByRequestEntityIdIn(List.of(result.getId()),
                Sort.by(Direction.ASC, "entityId"));
        if (!items.isEmpty()) {
            Collection<ItemShortDto> itemList = new ArrayList<>();
            for (Item item : items) {
                ItemShortDto itemShortDto = itemMapper.mapToShortDto(item);

                UserDto sharer = userMapper.mapToUserDto(item.getSharer());
                itemShortDto.setSharer(sharer);

                ItemRequestShortDto itemRequestShortDto = itemRequestMapper.mapToItemRequestShortDto(item.getRequest());
                itemShortDto.setRequest(itemRequestShortDto);

                itemList.add(itemShortDto);
            }

            result.setItems(itemList);
        }
        log.debug("Полученная модель преобразована");

        log.debug("Возврат результатов поиска на уровень контроллера");
        return result;
    }

    @Override
    public ItemRequestFullDto create(Long requestorId, ItemRequestCreateDto dto) {
        log.debug("Создание запроса на уровне сервиса");

        User requestor = userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + requestorId + " не найден"));
        log.debug("Передан идентификатор автора запроса: {}", requestor.getEntityId());

        ItemRequest itemRequest = itemRequestMapper.mapToItemRequest(dto);
        itemRequest.setRequestor(requestor);
        log.debug("Полученная модель преобразована для сохранения");

        itemRequest = itemRequestRepository.save(itemRequest);
        log.debug("После сохранения на уровень сервиса вернулся запрос с id {}", itemRequest.getEntityId());

        ItemRequestFullDto result = itemRequestMapper.mapToItemRequestFullDto(itemRequest);
        Collection<Item> items = itemRepository.findByRequestEntityIdIn(List.of(result.getId()),
                Sort.by(Direction.ASC, "entityId"));
        if (!items.isEmpty()) {
            Collection<ItemShortDto> itemList = new ArrayList<>();
            for (Item item : items) {
                ItemShortDto itemShortDto = itemMapper.mapToShortDto(item);

                UserDto sharer = userMapper.mapToUserDto(item.getSharer());
                itemShortDto.setSharer(sharer);

                ItemRequestShortDto itemRequestShortDto = itemRequestMapper.mapToItemRequestShortDto(item.getRequest());
                itemShortDto.setRequest(itemRequestShortDto);

                itemList.add(itemShortDto);
            }

            result.setItems(itemList);
        }
        log.debug("Сохраненная модель преобразована");

        log.debug("Возврат результатов сохранения на уровень контролера");
        return result;
    }

    private void completeModel(Collection<ItemRequestFullDto> result) {
        // Получим коллекцию всех вещей по списку идентификаторов запросов
        Collection<Item> items = itemRepository.findByRequestEntityIdIn(
                result.stream().map(ItemRequestFullDto::getId).toList(), Sort.by(Direction.ASC, "entityId"));

        // Если коллекция не пустая
        if (!items.isEmpty()) {
            for (ItemRequestFullDto dto : result) {
                Collection<ItemShortDto> itemList = new ArrayList<>();
                // Пополним коллекцию вещей модели
                for (Item item : items) {
                    if (item.getRequest().getEntityId().equals(dto.getId())) {
                        ItemShortDto itemShortDto = itemMapper.mapToShortDto(item);
                        itemShortDto.setSharer(userMapper.mapToUserDto(item.getSharer()));
                        itemShortDto.setRequest(itemRequestMapper.mapToItemRequestShortDto(item.getRequest()));
                        itemList.add(itemShortDto);
                    }
                }
                dto.setItems(itemList);
            }
        }
    }
}
