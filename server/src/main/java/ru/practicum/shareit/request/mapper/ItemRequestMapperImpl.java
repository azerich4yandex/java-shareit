package ru.practicum.shareit.request.mapper;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;


@RequiredArgsConstructor
@Service
@Slf4j
public class ItemRequestMapperImpl implements ItemRequestMapper {

    private final UserMapper userMapper;

    @Override
    public ItemRequestFullDto mapToItemRequestFullDto(ItemRequest itemRequest) {
        log.debug("Преобразование данных из модели {} в модель {}", ItemRequest.class,
                ItemRequestFullDto.class);
        return ItemRequestFullDto.builder()
                .id(itemRequest.getEntityId())
                .description(itemRequest.getDescription())
                .requestor(userMapper.mapToUserDto(itemRequest.getRequestor()))
                .created(itemRequest.getCreated())
                .build();
    }

    @Override
    public ItemRequestShortDto mapToItemRequestShortDto(ItemRequest itemRequest) {
        return ItemRequestShortDto.builder()
                .id(itemRequest.getEntityId())
                .description(itemRequest.getDescription())
                .requestor(userMapper.mapToUserDto(itemRequest.getRequestor()))
                .created(itemRequest.getCreated())
                .build();
    }

    @Override
    public ItemRequest mapToItemRequest(ItemRequestCreateDto dto) {
        log.debug("Преобразование данных из модели {} в краткую модель {}", ItemRequestCreateDto.class,
                ItemRequest.class);
        return ItemRequest.builder()
                .description(dto.getDescription())
                .created(LocalDateTime.now())
                .build();
    }
}
