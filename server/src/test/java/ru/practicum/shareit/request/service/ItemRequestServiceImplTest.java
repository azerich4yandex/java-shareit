package ru.practicum.shareit.request.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.commons.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@DisplayName("Обработка данных на уровне сервиса ItemRequestService")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {

    @Autowired
    private final ItemRequestServiceImpl itemRequestService;

    @MockBean
    private final ItemRequestRepository itemRequestRepository;

    @MockBean
    private final UserRepository userRepository;

    @MockBean
    private final BookingRepository bookingRepository;

    @MockBean
    private final CommentRepository commentRepository;

    @MockBean
    private final ItemRepository itemRepository;

    private User requestor;
    private ItemRequest itemRequest;
    private User owner;
    private Item item;
    private ItemRequestCreateDto itemRequestCreateDto;

    private static Page<ItemRequest> getPageFromList(List<ItemRequest> list) {
        return new PageImpl<>(list, PageRequest.of(0, 10), list.isEmpty() ? 1 : list.size());
    }

    @BeforeEach
    void setUp() {
        Random random = new Random();

        requestor = User.builder()
                .entityId(Math.abs(random.nextLong()))
                .name("Requestor Test")
                .email("requestor@system.com")
                .build();

        itemRequest = ItemRequest.builder()
                .entityId(Math.abs(random.nextLong()))
                .description("Request description")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();

        owner = User.builder()
                .entityId(Math.abs(random.nextLong()))
                .name("User Test")
                .email("owner@system.com")
                .build();

        item = Item.builder()
                .entityId(Math.abs(random.nextLong()))
                .name("Item name")
                .description("Item description")
                .available(true)
                .sharer(owner)
                .request(itemRequest)
                .build();

        itemRequestCreateDto = ItemRequestCreateDto.builder()
                .description(itemRequest.getDescription())
                .build();
    }

    @AfterEach
    void tearDown() {
        requestor = null;
        itemRequest = null;
        owner = null;
        item = null;
        itemRequestCreateDto = null;
    }

    @DisplayName("Получение списка всех запросов")
    @Test
    void findAll() {
        when(itemRequestRepository.findAll(any(Pageable.class)))
                .thenReturn(getPageFromList(List.of(itemRequest)));

        Collection<ItemRequestShortDto> requestList = itemRequestService.findAll(0, 10);
        assertNotNull(requestList);
        assertFalse(requestList.isEmpty());
        assertEquals(1, requestList.size());

        Optional<ItemRequestShortDto> itemRequestShortDtoOpt = requestList.stream().findFirst();
        assertNotNull(itemRequestShortDtoOpt);
        assertTrue(itemRequestShortDtoOpt.isPresent());

        ItemRequestShortDto itemRequestShortDto = itemRequestShortDtoOpt.get();
        assertNotNull(itemRequestShortDto);
        assertEquals(itemRequest.getEntityId(), itemRequestShortDto.getId());
        assertEquals(requestor.getEntityId(), itemRequestShortDto.getRequestor().getId());
        assertEquals(requestor.getName(), itemRequestShortDto.getRequestor().getName());
        assertEquals(requestor.getEmail(), itemRequestShortDto.getRequestor().getEmail());
        assertEquals(itemRequest.getDescription(), itemRequestShortDto.getDescription());
        assertEquals(itemRequest.getCreated(), itemRequestShortDto.getCreated());
    }

    @DisplayName("Получение пустого списка всех запросов")
    @Test
    void findAllEmptyList() {
        when(itemRequestRepository.findAll(any(Pageable.class)))
                .thenReturn(getPageFromList(new ArrayList<>()));

        Collection<ItemRequestShortDto> requestList = itemRequestService.findAll(0, 10);
        assertNotNull(requestList);
        assertTrue(requestList.isEmpty());
    }

    @DisplayName("Вызов исключения  при получении списка всех запросов")
    @Test
    void findAllWith500Exception() {
        when(itemRequestRepository.findAll(any(Pageable.class)))
                .thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> itemRequestService.findAll(0, 10));
    }

    @DisplayName("Получение списка бронирований по идентификатору автора")
    @Test
    void findByRequestorId() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(requestor));
        when(itemRequestRepository.findByRequestorEntityId(anyLong(), any()))
                .thenReturn(getPageFromList(List.of(itemRequest)));
        when(itemRepository.findByRequestEntityIdIn(anyList(), any()))
                .thenReturn(List.of(item));

        Collection<ItemRequestFullDto> requestList = itemRequestService.findByRequestorId(requestor.getEntityId(), 0,
                10);
        assertNotNull(requestList);
        assertFalse(requestList.isEmpty());
        assertEquals(1, requestList.size());

        Optional<ItemRequestFullDto> itemRequestFullDtoOpt = requestList.stream().findFirst();
        assertNotNull(itemRequestFullDtoOpt);
        assertTrue(itemRequestFullDtoOpt.isPresent());

        ItemRequestFullDto itemRequestFullDto = itemRequestFullDtoOpt.get();
        assertNotNull(itemRequestFullDto);
        assertEquals(itemRequest.getEntityId(), itemRequestFullDto.getId());
        assertEquals(itemRequest.getDescription(), itemRequestFullDto.getDescription());
        assertEquals(requestor.getEntityId(), itemRequestFullDto.getRequestor().getId());
        assertEquals(requestor.getName(), itemRequestFullDto.getRequestor().getName());
        assertEquals(requestor.getEmail(), itemRequestFullDto.getRequestor().getEmail());
        assertEquals(itemRequest.getCreated(), itemRequestFullDto.getCreated());
        assertNotNull(itemRequestFullDto.getItems());
        assertFalse(itemRequestFullDto.getItems().isEmpty());

        Collection<ItemShortDto> itemList = itemRequestFullDto.getItems();
        assertNotNull(itemList);
        assertEquals(1, itemList.size());

        Optional<ItemShortDto> itemShortDtoOpt = itemList.stream().findFirst();
        assertNotNull(itemShortDtoOpt);
        assertTrue(itemShortDtoOpt.isPresent());

        ItemShortDto itemShortDto = itemShortDtoOpt.get();
        assertNotNull(itemShortDto);
        assertEquals(item.getEntityId(), itemShortDto.getId());
        assertEquals(owner.getEntityId(), itemShortDto.getSharer().getId());
        assertEquals(owner.getName(), itemShortDto.getSharer().getName());
        assertEquals(owner.getEmail(), itemShortDto.getSharer().getEmail());
        assertEquals(item.getName(), itemShortDto.getName());
        assertEquals(item.getDescription(), itemShortDto.getDescription());
        assertEquals(item.getAvailable(), itemShortDto.getAvailable());
        assertEquals(itemRequest.getEntityId(), itemShortDto.getRequest().getId());
        assertEquals(requestor.getEntityId(), itemShortDto.getRequest().getRequestor().getId());
        assertEquals(requestor.getName(), itemShortDto.getRequest().getRequestor().getName());
        assertEquals(requestor.getEmail(), itemShortDto.getRequest().getRequestor().getEmail());
        assertEquals(itemRequest.getDescription(), itemShortDto.getRequest().getDescription());
        assertEquals(itemRequest.getCreated(), itemShortDto.getRequest().getCreated());
    }

    @DisplayName("Вызов исключения NotFoundException при получении списка бронирований по идентификатору автора")
    @Test
    void findByRequestorIdWith404Exception() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.findByRequestorId(requestor.getEntityId(), 0, 10));
    }

    @DisplayName("Вызов исключения RuntimeException при получении списка бронирований по идентификатору автора")
    @Test
    void findByRequestorIdWith500Exception() {
        when(userRepository.findById(anyLong()))
                .thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class,
                () -> itemRequestService.findByRequestorId(requestor.getEntityId(), 0, 10));
    }

    @DisplayName("Получение запроса по идентификатору")
    @Test
    void findById() {
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(itemRequest));
        when(itemRepository.findByRequestEntityIdIn(anyList(), any()))
                .thenReturn(List.of(item));

        ItemRequestFullDto itemRequestFullDto = itemRequestService.findById(itemRequest.getEntityId());
        assertNotNull(itemRequestFullDto);
        assertEquals(itemRequest.getEntityId(), itemRequestFullDto.getId());
        assertEquals(itemRequest.getDescription(), itemRequestFullDto.getDescription());
        assertEquals(requestor.getEntityId(), itemRequestFullDto.getRequestor().getId());
        assertEquals(requestor.getName(), itemRequestFullDto.getRequestor().getName());
        assertEquals(requestor.getEmail(), itemRequestFullDto.getRequestor().getEmail());
        assertEquals(itemRequest.getCreated(), itemRequestFullDto.getCreated());
        assertNotNull(itemRequestFullDto.getItems());
        assertFalse(itemRequestFullDto.getItems().isEmpty());

        Collection<ItemShortDto> itemList = itemRequestFullDto.getItems();
        assertNotNull(itemList);
        assertEquals(1, itemList.size());

        Optional<ItemShortDto> itemShortDtoOpt = itemList.stream().findFirst();
        assertNotNull(itemShortDtoOpt);
        assertTrue(itemShortDtoOpt.isPresent());

        ItemShortDto itemShortDto = itemShortDtoOpt.get();
        assertNotNull(itemShortDto);
        assertEquals(item.getEntityId(), itemShortDto.getId());
        assertEquals(owner.getEntityId(), itemShortDto.getSharer().getId());
        assertEquals(owner.getName(), itemShortDto.getSharer().getName());
        assertEquals(owner.getEmail(), itemShortDto.getSharer().getEmail());
        assertEquals(item.getName(), itemShortDto.getName());
        assertEquals(item.getDescription(), itemShortDto.getDescription());
        assertEquals(item.getAvailable(), itemShortDto.getAvailable());
        assertEquals(itemRequest.getEntityId(), itemShortDto.getRequest().getId());
        assertEquals(requestor.getEntityId(), itemShortDto.getRequest().getRequestor().getId());
        assertEquals(requestor.getName(), itemShortDto.getRequest().getRequestor().getName());
        assertEquals(requestor.getEmail(), itemShortDto.getRequest().getRequestor().getEmail());
        assertEquals(itemRequest.getDescription(), itemShortDto.getRequest().getDescription());
        assertEquals(itemRequest.getCreated(), itemShortDto.getRequest().getCreated());
    }

    @DisplayName("Вызов исключения  при получении запроса по идентификатору")
    @Test
    void findByIdWith404Exception() {
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.findById(itemRequest.getEntityId()));
    }

    @DisplayName("Создание запроса")
    @Test
    void create() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(requestor));
        when(itemRequestRepository.save(any()))
                .thenReturn(itemRequest);
        when(itemRepository.findByRequestEntityIdIn(anyList(), any()))
                .thenReturn(List.of(item));

        ItemRequestFullDto itemRequestFullDto = itemRequestService.create(requestor.getEntityId(),
                itemRequestCreateDto);
        assertNotNull(itemRequestFullDto);
        assertEquals(itemRequest.getEntityId(), itemRequestFullDto.getId());
        assertEquals(itemRequest.getDescription(), itemRequestFullDto.getDescription());
        assertEquals(requestor.getEntityId(), itemRequestFullDto.getRequestor().getId());
        assertEquals(requestor.getName(), itemRequestFullDto.getRequestor().getName());
        assertEquals(requestor.getEmail(), itemRequestFullDto.getRequestor().getEmail());
        assertEquals(itemRequest.getCreated(), itemRequestFullDto.getCreated());
        assertNotNull(itemRequestFullDto.getItems());
        assertFalse(itemRequestFullDto.getItems().isEmpty());

        Collection<ItemShortDto> itemList = itemRequestFullDto.getItems();
        assertNotNull(itemList);
        assertEquals(1, itemList.size());

        Optional<ItemShortDto> itemShortDtoOpt = itemList.stream().findFirst();
        assertNotNull(itemShortDtoOpt);
        assertTrue(itemShortDtoOpt.isPresent());

        ItemShortDto itemShortDto = itemShortDtoOpt.get();
        assertNotNull(itemShortDto);
        assertEquals(item.getEntityId(), itemShortDto.getId());
        assertEquals(owner.getEntityId(), itemShortDto.getSharer().getId());
        assertEquals(owner.getName(), itemShortDto.getSharer().getName());
        assertEquals(owner.getEmail(), itemShortDto.getSharer().getEmail());
        assertEquals(item.getName(), itemShortDto.getName());
        assertEquals(item.getDescription(), itemShortDto.getDescription());
        assertEquals(item.getAvailable(), itemShortDto.getAvailable());
        assertEquals(itemRequest.getEntityId(), itemShortDto.getRequest().getId());
        assertEquals(requestor.getEntityId(), itemShortDto.getRequest().getRequestor().getId());
        assertEquals(requestor.getName(), itemShortDto.getRequest().getRequestor().getName());
        assertEquals(requestor.getEmail(), itemShortDto.getRequest().getRequestor().getEmail());
        assertEquals(itemRequest.getDescription(), itemShortDto.getRequest().getDescription());
        assertEquals(itemRequest.getCreated(), itemShortDto.getRequest().getCreated());
    }

    @DisplayName("Вызов исключения NotFoundException при создании запроса")
    @Test
    void createWith404Exception() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.create(requestor.getEntityId(), itemRequestCreateDto));
    }

    @DisplayName("Вызов исключения RuntimeException при создании запроса")
    @Test
    void createWith500Exception() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(requestor));
        when(itemRequestRepository.save(any()))
                .thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class,
                () -> itemRequestService.create(requestor.getEntityId(), itemRequestCreateDto));
    }
}