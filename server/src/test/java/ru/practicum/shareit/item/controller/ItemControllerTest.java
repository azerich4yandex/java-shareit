package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.commons.exceptions.IncorrectDataException;
import ru.practicum.shareit.commons.exceptions.NotFoundException;
import ru.practicum.shareit.commons.exceptions.UserIsNotSharerException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Обработка REST-запросов к /items")
@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    private static final String X_SHARER_HEADER = "X-Sharer-User-Id";

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ItemService itemService;

    @Autowired
    MockMvc mockMvc;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private UserDto owner;
    private UserDto booker;
    private BookingShortDto lastBookingShortDto;
    private BookingShortDto nextBookingShortDto;
    private CommentShortDto commentShortDto;
    private ItemRequestShortDto itemRequestShortDto;
    private ItemShortDto itemShortDto;
    private ItemRequestFullDto itemRequestFullDto;
    private ItemFullDto itemFullDto;
    private ItemCreateDto itemCreateDto;
    private CommentCreateDto commentCreateDto;
    private ItemUpdateDto itemUpdateDto;

    @BeforeEach
    void init() {
        Random random = new Random();

        startDate = LocalDateTime.now().plusDays(-2);
        endDate = LocalDateTime.now().plusDays(-1);

        owner = UserDto.builder()
                .id(Math.abs(random.nextLong()))
                .name("User Test")
                .email("owner@system.com")
                .build();

        booker = UserDto.builder()
                .id(Math.abs(random.nextLong()))
                .name("User Test")
                .email("booker@system.com")
                .build();

        lastBookingShortDto = BookingShortDto.builder()
                .id(Math.abs(random.nextLong()))
                .bookerId(booker.getId())
                .start(startDate)
                .end(endDate)
                .build();

        nextBookingShortDto = BookingShortDto.builder()
                .id(Math.abs(random.nextLong()))
                .bookerId(booker.getId())
                .start(lastBookingShortDto.getStart().plusDays(3))
                .end(lastBookingShortDto.getEnd().plusDays(3))
                .build();

        commentShortDto = CommentShortDto.builder()
                .id(Math.abs(random.nextLong()))
                .text("Comment text")
                .authorName(booker.getName())
                .created(lastBookingShortDto.getEnd().plusHours(1))
                .build();

        itemRequestShortDto = ItemRequestShortDto.builder()
                .id(Math.abs(random.nextLong()))
                .requestor(booker)
                .description("Item request description")
                .created(LocalDateTime.now().minusDays(3))
                .build();

        itemShortDto = ItemShortDto.builder()
                .id(Math.abs(random.nextLong()))
                .name("Item name")
                .description("Item description")
                .sharer(owner)
                .available(true)
                .request(itemRequestShortDto)
                .build();

        itemRequestFullDto = ItemRequestFullDto.builder()
                .id(itemRequestShortDto.getId())
                .description(itemRequestShortDto.getDescription())
                .created(itemRequestShortDto.getCreated())
                .items(List.of(itemShortDto))
                .requestor(itemRequestShortDto.getRequestor())
                .build();

        itemFullDto = ItemFullDto.builder()
                .id(itemShortDto.getId())
                .sharer(itemShortDto.getSharer())
                .name(itemShortDto.getName())
                .description(itemShortDto.getDescription())
                .available(itemShortDto.getAvailable())
                .lastBooking(lastBookingShortDto)
                .nextBooking(nextBookingShortDto)
                .comments(List.of(commentShortDto))
                .request(itemRequestFullDto)
                .build();

        itemCreateDto = ItemCreateDto.builder()
                .name(itemShortDto.getName())
                .description(itemShortDto.getDescription())
                .available(itemShortDto.getAvailable())
                .requestId(itemRequestShortDto.getId())
                .build();

        commentCreateDto = CommentCreateDto.builder()
                .text(commentShortDto.getText())
                .authorId(booker.getId())
                .created(commentShortDto.getCreated())
                .build();

        itemUpdateDto = ItemUpdateDto.builder()
                .itemId(itemFullDto.getId())
                .name(itemFullDto.getName())
                .description(itemFullDto.getDescription())
                .available(itemFullDto.getAvailable())
                .build();
    }

    @AfterEach
    void halt() {
        startDate = null;
        endDate = null;

        owner = null;
        booker = null;

        lastBookingShortDto = null;
        nextBookingShortDto = null;

        commentShortDto = null;

        itemRequestShortDto = null;
        itemShortDto = null;

        itemRequestFullDto = null;

        itemFullDto = null;
        itemCreateDto = null;
        itemUpdateDto = null;
    }

    @DisplayName("Получение списка вещей")
    @Test
    void findAll() throws Exception {
        when(itemService.findAllByOwner(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemFullDto));

        mockMvc.perform(get("/items")
                        .header(X_SHARER_HEADER, owner.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemFullDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemFullDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemFullDto.getDescription())))
                .andExpect(jsonPath("$[0].sharer.id", is(owner.getId()), Long.class))
                .andExpect(jsonPath("$[0].sharer.name", is(owner.getName())))
                .andExpect(jsonPath("$[0].sharer.email", is(owner.getEmail())))
                .andExpect(jsonPath("$[0].lastBooking.id", is(lastBookingShortDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].lastBooking.bookerId", is(booker.getId()), Long.class))
                .andExpect(jsonPath("$[0].lastBooking.start",
                        is(lastBookingShortDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].lastBooking.end",
                        is(lastBookingShortDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].nextBooking.id", is(nextBookingShortDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].nextBooking.bookerId", is(booker.getId()), Long.class))
                .andExpect(jsonPath("$[0].nextBooking.start",
                        is(nextBookingShortDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].nextBooking.end",
                        is(nextBookingShortDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].comments[0].id", is(commentShortDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].comments[0].text", is(commentShortDto.getText())))
                .andExpect(jsonPath("$[0].comments[0].authorName", is(booker.getName())))
                .andExpect(jsonPath("$[0].comments[0].created",
                        is(commentShortDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].request.id", is(itemRequestFullDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].request.description", is(itemRequestFullDto.getDescription())))
                .andExpect(jsonPath("$[0].request.created",
                        is(itemRequestFullDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].request.requestor.id", is(booker.getId()), Long.class))
                .andExpect(jsonPath("$[0].request.requestor.name", is(booker.getName())))
                .andExpect(jsonPath("$[0].request.requestor.email", is(booker.getEmail())))
                .andExpect(jsonPath("$[0].request.items[0].id", is(itemShortDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].request.items[0].name", is(itemShortDto.getName())))
                .andExpect(jsonPath("$[0].request.items[0].description", is(itemShortDto.getDescription())))
                .andExpect(jsonPath("$[0].request.items[0].available", is(itemShortDto.getAvailable())))
                .andExpect(jsonPath("$[0].request.items[0].sharer.id", is(owner.getId()), Long.class))
                .andExpect(jsonPath("$[0].request.items[0].sharer.name", is(owner.getName())))
                .andExpect(jsonPath("$[0].request.items[0].sharer.email", is(owner.getEmail())))
                .andExpect(jsonPath("$[0].request.items[0].request.id", is(itemRequestShortDto.getId()), Long.class))
                .andExpect(
                        jsonPath("$[0].request.items[0].request.description", is(itemRequestShortDto.getDescription())))
                .andExpect(jsonPath("$[0].request.items[0].request.created",
                        is(itemRequestShortDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].request.items[0].request.requestor.id", is(booker.getId()), Long.class))
                .andExpect(jsonPath("$[0].request.items[0].request.requestor.name", is(booker.getName())))
                .andExpect(jsonPath("$[0].request.items[0].request.requestor.email", is(booker.getEmail())));

    }

    @DisplayName("Вызов исключения UserIsNotSharerException при получении списка вещей")
    @Test
    void findAllWith403Exception() throws Exception {
        when(itemService.findAllByOwner(anyLong(), anyInt(), anyInt()))
                .thenThrow(UserIsNotSharerException.class);

        mockMvc.perform(get("/items")
                        .header(X_SHARER_HEADER, owner.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isForbidden());
    }

    @DisplayName("Вызов исключения NotFoundException при получении списка вещей")
    @Test
    void findAllWith404Exception() throws Exception {
        when(itemService.findAllByOwner(anyLong(), anyInt(), anyInt()))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(get("/items")
                        .header(X_SHARER_HEADER, owner.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isNotFound());
    }

    @DisplayName("Вызов исключения RuntimeException при получении списка вещей")
    @Test
    void findAllWith500Exception() throws Exception {
        when(itemService.findAllByOwner(anyLong(), anyInt(), anyInt()))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(get("/items")
                        .header(X_SHARER_HEADER, owner.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().is5xxServerError())
                .andExpect(status().isInternalServerError());
    }

    @DisplayName("Поиск вещей по поисковой строке")
    @Test
    void findByText() throws Exception {
        when(itemService.findByText(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemShortDto));

        mockMvc.perform(get("/items/search?text=" + itemShortDto.getName())
                        .header(X_SHARER_HEADER, owner.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("text", itemShortDto.getName())
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemShortDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemShortDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemShortDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemShortDto.getAvailable())))
                .andExpect(jsonPath("$[0].sharer.id", is(owner.getId())))
                .andExpect(jsonPath("$[0].sharer.name", is(owner.getName())))
                .andExpect(jsonPath("$[0].sharer.email", is(owner.getEmail())))
                .andExpect(jsonPath("$[0].request.id", is(itemRequestShortDto.getId())))
                .andExpect(jsonPath("$[0].request.description", is(itemRequestShortDto.getDescription())))
                .andExpect(jsonPath("$[0].request.created",
                        is(itemRequestShortDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].request.requestor.id", is(booker.getId()), Long.class))
                .andExpect(jsonPath("$[0].request.requestor.name", is(booker.getName())))
                .andExpect(jsonPath("$[0].request.requestor.email", is(booker.getEmail())));
    }

    @DisplayName("Поиск вещей по пустой поисковой строке")
    @Test
    void findByTextWithEmptyCollection() throws Exception {
        mockMvc.perform(get("/items/search?text=")
                        .header(X_SHARER_HEADER, owner.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("text", "")
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @DisplayName("Вызов исключения ValidationException при поиске вещей по поисковой строке")
    @Test
    void findByTextWith400Exception() throws Exception {
        when(itemService.findByText(anyString(), anyInt(), anyInt()))
                .thenThrow(IncorrectDataException.class);

        mockMvc.perform(get("/items/search?text=" + itemShortDto.getName())
                        .header(X_SHARER_HEADER, owner.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("text", itemShortDto.getName())
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Вызов исключения RuntimeException при поиске вещей по поисковой строке")
    @Test
    void findByTextWith500Exception() throws Exception {
        when(itemService.findByText(anyString(), anyInt(), anyInt()))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(get("/items/search?text=" + itemShortDto.getName())
                        .header(X_SHARER_HEADER, owner.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("text", itemShortDto.getName())
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().is5xxServerError())
                .andExpect(status().isInternalServerError());
    }

    @DisplayName("Поиск вещи по идентификатору")
    @Test
    void findById() throws Exception {
        when(itemService.findById(anyLong(), anyLong()))
                .thenReturn(itemFullDto);

        mockMvc.perform(get("/items/" + itemFullDto.getId())
                        .header(X_SHARER_HEADER, owner.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemFullDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemFullDto.getName())))
                .andExpect(jsonPath("$.description", is(itemFullDto.getDescription())))
                .andExpect(jsonPath("$.sharer.id", is(owner.getId()), Long.class))
                .andExpect(jsonPath("$.sharer.name", is(owner.getName())))
                .andExpect(jsonPath("$.sharer.email", is(owner.getEmail())))
                .andExpect(jsonPath("$.lastBooking.id", is(lastBookingShortDto.getId()), Long.class))
                .andExpect(jsonPath("$.lastBooking.bookerId", is(booker.getId()), Long.class))
                .andExpect(jsonPath("$.lastBooking.start",
                        is(lastBookingShortDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.lastBooking.end",
                        is(lastBookingShortDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.nextBooking.id", is(nextBookingShortDto.getId()), Long.class))
                .andExpect(jsonPath("$.nextBooking.bookerId", is(booker.getId()), Long.class))
                .andExpect(jsonPath("$.nextBooking.start",
                        is(nextBookingShortDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.nextBooking.end",
                        is(nextBookingShortDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.comments[0].id", is(commentShortDto.getId()), Long.class))
                .andExpect(jsonPath("$.comments[0].text", is(commentShortDto.getText())))
                .andExpect(jsonPath("$.comments[0].authorName", is(booker.getName())))
                .andExpect(jsonPath("$.comments[0].created",
                        is(commentShortDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.request.id", is(itemRequestFullDto.getId()), Long.class))
                .andExpect(jsonPath("$.request.description", is(itemRequestFullDto.getDescription())))
                .andExpect(jsonPath("$.request.created",
                        is(itemRequestFullDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.request.requestor.id", is(booker.getId()), Long.class))
                .andExpect(jsonPath("$.request.requestor.name", is(booker.getName())))
                .andExpect(jsonPath("$.request.requestor.email", is(booker.getEmail())))
                .andExpect(jsonPath("$.request.items[0].id", is(itemShortDto.getId()), Long.class))
                .andExpect(jsonPath("$.request.items[0].name", is(itemShortDto.getName())))
                .andExpect(jsonPath("$.request.items[0].description", is(itemShortDto.getDescription())))
                .andExpect(jsonPath("$.request.items[0].available", is(itemShortDto.getAvailable())))
                .andExpect(jsonPath("$.request.items[0].sharer.id", is(owner.getId()), Long.class))
                .andExpect(jsonPath("$.request.items[0].sharer.name", is(owner.getName())))
                .andExpect(jsonPath("$.request.items[0].sharer.email", is(owner.getEmail())))
                .andExpect(jsonPath("$.request.items[0].request.id", is(itemRequestShortDto.getId()), Long.class))
                .andExpect(jsonPath("$.request.items[0].request.description", is(itemRequestShortDto.getDescription())))
                .andExpect(jsonPath("$.request.items[0].request.created",
                        is(itemRequestShortDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.request.items[0].request.requestor.id", is(booker.getId()), Long.class))
                .andExpect(jsonPath("$.request.items[0].request.requestor.name", is(booker.getName())))
                .andExpect(jsonPath("$.request.items[0].request.requestor.email", is(booker.getEmail())));
    }

    @DisplayName("Вызов исключения ValidationException при поиске вещи по идентификатору")
    @Test
    void findByIdWith400Exception() throws Exception {
        when(itemService.findById(anyLong(), anyLong()))
                .thenThrow(IncorrectDataException.class);

        mockMvc.perform(get("/items/" + itemFullDto.getId())
                        .header(X_SHARER_HEADER, owner.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Вызов исключения UserIsNotSharerException при поиске вещи по идентификатору")
    @Test
    void findByIdWith403Exception() throws Exception {
        when(itemService.findById(anyLong(), anyLong()))
                .thenThrow(UserIsNotSharerException.class);

        mockMvc.perform(get("/items/" + itemFullDto.getId())
                        .header(X_SHARER_HEADER, owner.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isForbidden());
    }

    @DisplayName("Вызов исключения NotFoundException при поиске вещи по идентификатору")
    @Test
    void findByIdWith404Exception() throws Exception {
        when(itemService.findById(anyLong(), anyLong()))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(get("/items/" + itemFullDto.getId())
                        .header(X_SHARER_HEADER, owner.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isNotFound());
    }

    @DisplayName("Вызов исключения RuntimeException при поиске вещи по идентификатору")
    @Test
    void findByIdWith500Exception() throws Exception {
        when(itemService.findById(anyLong(), anyLong()))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(get("/items/" + itemFullDto.getId())
                        .header(X_SHARER_HEADER, owner.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is5xxServerError())
                .andExpect(status().isInternalServerError());
    }

    @DisplayName("Создание вещи по запросу")
    @Test
    void createItem() throws Exception {
        when(itemService.create(anyLong(), any()))
                .thenReturn(itemShortDto);

        mockMvc.perform(post("/items")
                        .header(X_SHARER_HEADER, owner.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(itemCreateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemShortDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemShortDto.getName())))
                .andExpect(jsonPath("$.description", is(itemShortDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemShortDto.getAvailable())))
                .andExpect(jsonPath("$.sharer.id", is(owner.getId())))
                .andExpect(jsonPath("$.sharer.name", is(owner.getName())))
                .andExpect(jsonPath("$.sharer.email", is(owner.getEmail())))
                .andExpect(jsonPath("$.request.id", is(itemRequestShortDto.getId())))
                .andExpect(jsonPath("$.request.description", is(itemRequestShortDto.getDescription())))
                .andExpect(jsonPath("$.request.created",
                        is(itemRequestShortDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.request.requestor.id", is(booker.getId()), Long.class))
                .andExpect(jsonPath("$.request.requestor.name", is(booker.getName())))
                .andExpect(jsonPath("$.request.requestor.email", is(booker.getEmail())));
    }

    @DisplayName("Создание вещи без запроса")
    @Test
    void createItemWithoutRequest() throws Exception {
        itemCreateDto.setRequestId(null);
        itemShortDto.setRequest(null);

        when(itemService.create(anyLong(), any()))
                .thenReturn(itemShortDto);

        mockMvc.perform(post("/items")
                        .header(X_SHARER_HEADER, owner.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(itemCreateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemShortDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemShortDto.getName())))
                .andExpect(jsonPath("$.description", is(itemShortDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemShortDto.getAvailable())))
                .andExpect(jsonPath("$.sharer.id", is(owner.getId())))
                .andExpect(jsonPath("$.sharer.name", is(owner.getName())))
                .andExpect(jsonPath("$.sharer.email", is(owner.getEmail())))
                .andExpect(jsonPath("$.request").isEmpty());
    }

    @DisplayName("Вызов исключения ValidationException при создании вещи по запросу")
    @Test
    void createItemWith400Exception() throws Exception {
        when(itemService.create(anyLong(), any()))
                .thenThrow(IncorrectDataException.class);

        mockMvc.perform(post("/items")
                        .header(X_SHARER_HEADER, owner.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(itemCreateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Вызов исключения NotFoundException при создании вещи по запросу")
    @Test
    void createItemWith404Exception() throws Exception {
        when(itemService.create(anyLong(), any()))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(post("/items")
                        .header(X_SHARER_HEADER, owner.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(itemCreateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isNotFound());
    }

    @DisplayName("Вызов исключения RuntimeException при создании вещи по запросу")
    @Test
    void createItemWith500Exception() throws Exception {
        when(itemService.create(anyLong(), any()))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(post("/items")
                        .header(X_SHARER_HEADER, owner.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(itemCreateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andExpect(status().isInternalServerError());
    }

    @DisplayName("Создание комментария на завершённую аренду")
    @Test
    void createComment() throws Exception {
        when(itemService.createComment(anyLong(), anyLong(), any()))
                .thenReturn(commentShortDto);

        mockMvc.perform(post("/items/" + itemShortDto.getId() + "/comment")
                        .header(X_SHARER_HEADER, booker.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(commentCreateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentShortDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentShortDto.getText())))
                .andExpect(jsonPath("$.authorName", is(booker.getName())))
                .andExpect(jsonPath("$.created",
                        is(commentShortDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))));
    }

    @DisplayName("Вызов исключения ValidationException при добавлении комментария")
    @Test
    void createCommentWith400Exception() throws Exception {
        when(itemService.createComment(anyLong(), anyLong(), any()))
                .thenThrow(IncorrectDataException.class);

        mockMvc.perform(post("/items/" + itemShortDto.getId() + "/comment")
                        .header(X_SHARER_HEADER, booker.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(commentCreateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Вызов исключения UserIsNotSharerException при добавлении комментария")
    @Test
    void createCommentWith403Exception() throws Exception {
        when(itemService.createComment(anyLong(), anyLong(), any()))
                .thenThrow(UserIsNotSharerException.class);

        mockMvc.perform(post("/items/" + itemShortDto.getId() + "/comment")
                        .header(X_SHARER_HEADER, booker.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(commentCreateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isForbidden());
    }

    @DisplayName("Вызов исключения NotFoundException при добавлении комментария")
    @Test
    void createCommentWith404Exception() throws Exception {
        when(itemService.createComment(anyLong(), anyLong(), any()))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(post("/items/" + itemShortDto.getId() + "/comment")
                        .header(X_SHARER_HEADER, booker.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(commentCreateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isNotFound());
    }

    @DisplayName("Вызов исключения RuntimeException при добавлении комментария")
    @Test
    void createCommentWith500Exception() throws Exception {
        when(itemService.createComment(anyLong(), anyLong(), any()))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(post("/items/" + itemShortDto.getId() + "/comment")
                        .header(X_SHARER_HEADER, booker.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(commentCreateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andExpect(status().isInternalServerError());
    }

    @DisplayName("Обновление вещи по идентификатору")
    @Test
    void updateItem() throws Exception {
        when(itemService.update(anyLong(), anyLong(), any()))
                .thenReturn(itemShortDto);

        mockMvc.perform(patch("/items/" + itemUpdateDto.getItemId())
                        .header(X_SHARER_HEADER, owner.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(itemUpdateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemShortDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemShortDto.getName())))
                .andExpect(jsonPath("$.description", is(itemShortDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemShortDto.getAvailable())))
                .andExpect(jsonPath("$.sharer.id", is(owner.getId())))
                .andExpect(jsonPath("$.sharer.name", is(owner.getName())))
                .andExpect(jsonPath("$.sharer.email", is(owner.getEmail())))
                .andExpect(jsonPath("$.request.id", is(itemRequestShortDto.getId())))
                .andExpect(jsonPath("$.request.description", is(itemRequestShortDto.getDescription())))
                .andExpect(jsonPath("$.request.created", is(itemRequestShortDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.request.requestor.id", is(booker.getId()), Long.class))
                .andExpect(jsonPath("$.request.requestor.name", is(booker.getName())))
                .andExpect(jsonPath("$.request.requestor.email", is(booker.getEmail())));
    }

    @DisplayName("Вызов исключения ValidationException при обновлении по идентификатору вещи")
    @Test
    void updateItemWith400Exception() throws Exception {
        when(itemService.update(anyLong(), anyLong(), any()))
                .thenThrow(IncorrectDataException.class);

        mockMvc.perform(patch("/items/" + itemUpdateDto.getItemId())
                        .header(X_SHARER_HEADER, owner.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(itemUpdateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Вызов исключения UserIsNotSharerException при обновлении по идентификатору вещи")
    @Test
    void updateItemWith403Exception() throws Exception {
        when(itemService.update(anyLong(), anyLong(), any()))
                .thenThrow(UserIsNotSharerException.class);

        mockMvc.perform(patch("/items/" + itemUpdateDto.getItemId())
                        .header(X_SHARER_HEADER, owner.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(itemUpdateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isForbidden());
    }

    @DisplayName("Вызов исключения NotFoundException при обновлении по идентификатору вещи")
    @Test
    void updateItemWith404Exception() throws Exception {
        when(itemService.update(anyLong(), anyLong(), any()))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(patch("/items/" + itemUpdateDto.getItemId())
                        .header(X_SHARER_HEADER, owner.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(itemUpdateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isNotFound());
    }

    @DisplayName("Вызов исключения RuntimeException при обновлении по идентификатору вещи")
    @Test
    void updateItemWith500Exception() throws Exception {
        when(itemService.update(anyLong(), anyLong(), any()))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(patch("/items/" + itemUpdateDto.getItemId())
                        .header(X_SHARER_HEADER, owner.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(itemUpdateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andExpect(status().isInternalServerError());
    }

    @DisplayName("Удаление вещи по идентификатору")
    @Test
    void deleteItem() throws Exception {
        mockMvc.perform(delete("/items/" + itemFullDto.getId())
                .header(X_SHARER_HEADER, owner.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(itemService).delete(owner.getId(), itemFullDto.getId());
    }
}