package ru.practicum.shareit.request.mapper;

import java.time.LocalDateTime;
import java.util.Random;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Проверка работы маппера ItemRequestMapper")
class ItemRequestMapperImplTest {

    private UserMapperImpl userMapper;
    private ItemRequestMapperImpl itemRequestMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapperImpl();
        itemRequestMapper = new ItemRequestMapperImpl(userMapper);
    }

    @AfterEach
    void tearDown() {
        itemRequestMapper = null;
        userMapper = null;
    }

    @DisplayName("Проверка преобразования из ItemRequest в ItemRequestFullDto")
    @Test
    void mapToItemRequestFullDto() {
        Random random = new Random();

        User requestor = User.builder()
                .entityId(Math.abs(random.nextLong()))
                .name("Requestor Test")
                .email("requestor@system.com")
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .entityId(Math.abs(random.nextLong()))
                .description("Request description")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();

        ItemRequestFullDto itemRequestFullDto = itemRequestMapper.mapToItemRequestFullDto(itemRequest);
        assertNotNull(itemRequestFullDto);
        assertEquals(itemRequest.getEntityId(), itemRequestFullDto.getId());
        assertEquals(itemRequest.getDescription(), itemRequestFullDto.getDescription());
        assertEquals(requestor.getEntityId(), itemRequestFullDto.getRequestor().getId());
        assertEquals(requestor.getName(), itemRequestFullDto.getRequestor().getName());
        assertEquals(requestor.getEmail(), itemRequestFullDto.getRequestor().getEmail());
        assertEquals(itemRequest.getCreated(), itemRequestFullDto.getCreated());
    }

    @DisplayName("Проверка преобразования из ItemRequest в ItemRequestShortDto")
    @Test
    void mapToItemRequestShortDto() {
        Random random = new Random();

        User requestor = User.builder()
                .entityId(Math.abs(random.nextLong()))
                .name("Requestor Test")
                .email("requestor@system.com")
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .entityId(Math.abs(random.nextLong()))
                .description("Request description")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();

        ItemRequestShortDto itemRequestShortDto = itemRequestMapper.mapToItemRequestShortDto(itemRequest);
        assertNotNull(itemRequestShortDto);
        assertEquals(itemRequest.getEntityId(), itemRequestShortDto.getId());
        assertEquals(itemRequest.getDescription(), itemRequestShortDto.getDescription());
        assertEquals(requestor.getEntityId(), itemRequestShortDto.getRequestor().getId());
        assertEquals(requestor.getName(), itemRequestShortDto.getRequestor().getName());
        assertEquals(requestor.getEmail(), itemRequestShortDto.getRequestor().getEmail());
        assertEquals(itemRequest.getCreated(), itemRequestShortDto.getCreated());
    }

    @DisplayName("Проверка преобразования из ItemRequestCreateDto в ItemRequest")
    @Test
    void mapToItemRequest() {
        ItemRequestCreateDto itemRequestCreateDto = ItemRequestCreateDto.builder()
                .description("Request description")
                .build();

        ItemRequest itemRequest = itemRequestMapper.mapToItemRequest(itemRequestCreateDto);
        assertNotNull(itemRequest);
        assertEquals(itemRequestCreateDto.getDescription(), itemRequest.getDescription());
        assertNotNull(itemRequest.getCreated());
    }
}