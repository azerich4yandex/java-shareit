package ru.practicum.shareit.booking.controller;

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
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.commons.exceptions.IncorrectDataException;
import ru.practicum.shareit.commons.exceptions.NotFoundException;
import ru.practicum.shareit.commons.exceptions.UserIsNotSharerException;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Обработка REST-запросов к /bookings")
@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    private static final String X_SHARER_HEADER = "X-Sharer-User-Id";

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    MockMvc mockMvc;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private UserDto ownerDto;
    private UserDto bookerDto;
    private ItemShortDto itemShortDto;
    private BookingFullDto bookingFullDto;
    private BookingCreateDto bookingCreateDto;

    @BeforeEach
    void setUp() {
        Random random = new Random();
        startDate = LocalDateTime.now().plusDays(1);
        endDate = LocalDateTime.now().plusDays(2);

        ownerDto = UserDto.builder()
                .id(Math.abs(random.nextLong()))
                .name("Owner Test")
                .email("owner@system.com")
                .build();

        bookerDto = UserDto.builder()
                .id(Math.abs(random.nextLong()))
                .name("Booker Test")
                .email("booker@system.com")
                .build();

        itemShortDto = ItemShortDto.builder()
                .id(Math.abs(random.nextLong()))
                .name("Test item")
                .sharer(ownerDto)
                .build();

        bookingFullDto = BookingFullDto.builder()
                .id(Math.abs(random.nextLong()))
                .start(startDate)
                .end(endDate)
                .item(itemShortDto)
                .booker(bookerDto)
                .status(BookingStatus.WAITING)
                .build();

        bookingCreateDto = BookingCreateDto.builder()
                .start(bookingFullDto.getStart())
                .end(bookingFullDto.getEnd())
                .itemId(bookingFullDto.getItem().getId())
                .bookerId(bookerDto.getId())
                .build();
    }

    @AfterEach
    void tearDown() {
        startDate = null;
        endDate = null;

        ownerDto = null;
        bookerDto = null;

        itemShortDto = null;

        bookingFullDto = null;
        bookingCreateDto = null;
    }

    @DisplayName("Получение списка бронирований по идентификатору автора бронирований")
    @Test
    void getAllBookingsByBooker() throws Exception {
        when(bookingService.findAllByBookerAndState(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingFullDto));

        mockMvc.perform(get("/bookings")
                        .header(X_SHARER_HEADER, bookerDto.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingFullDto.getId()), Long.class))
                .andExpect(
                        jsonPath("$[0].start", is(bookingFullDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].end", is(bookingFullDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].item.id", is(itemShortDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(itemShortDto.getName())))
                .andExpect(jsonPath("$[0].item.sharer.id", is(ownerDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.sharer.name", is(ownerDto.getName())))
                .andExpect(jsonPath("$[0].item.sharer.email", is(ownerDto.getEmail())))
                .andExpect(jsonPath("$[0].booker.id", is(bookerDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.name", is(bookerDto.getName())))
                .andExpect(jsonPath("$[0].booker.email", is(bookerDto.getEmail())))
                .andExpect(jsonPath("$[0].status", is(bookingFullDto.getStatus().toString())));
    }

    @DisplayName("Вызов исключения ValidationException при получении списка бронирований по идентификатору бронирующего")
    @Test
    void getAllBookingsByBookerWith400Exception() throws Exception {
        when(bookingService.findAllByBookerAndState(anyLong(), any(), anyInt(), anyInt()))
                .thenThrow(IncorrectDataException.class);
        mockMvc.perform(get("/bookings")
                        .header(X_SHARER_HEADER, ownerDto.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Вызов исключения NotFoundException при получении списка бронирований по идентификатору бронирующего")
    @Test
    void getAllBookingsByBookerWith404Exception() throws Exception {
        when(bookingService.findAllByBookerAndState(anyLong(), any(), anyInt(), anyInt()))
                .thenThrow(NotFoundException.class);
        mockMvc.perform(get("/bookings")
                        .header(X_SHARER_HEADER, ownerDto.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isNotFound());
    }

    @DisplayName("Вызов исключения RuntimeException при получении списка бронирований по идентификатору бронирующего")
    @Test
    void getAllBookingsByBookerWith500Exception() throws Exception {
        when(bookingService.findAllByBookerAndState(anyLong(), any(), anyInt(), anyInt()))
                .thenThrow(RuntimeException.class);
        mockMvc.perform(get("/bookings")
                        .header(X_SHARER_HEADER, ownerDto.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().is5xxServerError())
                .andExpect(status().isInternalServerError());
    }

    @DisplayName("Получение списка бронирований по идентификатору владельца вещи")
    @Test
    void getAllBookingByOwner() throws Exception {
        when(bookingService.findAllByOwnerAndState(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingFullDto));

        mockMvc.perform(get("/bookings/owner")
                        .header(X_SHARER_HEADER, ownerDto.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingFullDto.getId()), Long.class))
                .andExpect(
                        jsonPath("$[0].start", is(bookingFullDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].end", is(bookingFullDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].item.id", is(itemShortDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(itemShortDto.getName())))
                .andExpect(jsonPath("$[0].item.sharer.id", is(ownerDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.sharer.name", is(ownerDto.getName())))
                .andExpect(jsonPath("$[0].item.sharer.email", is(ownerDto.getEmail())))
                .andExpect(jsonPath("$[0].booker.id", is(bookerDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.name", is(bookerDto.getName())))
                .andExpect(jsonPath("$[0].booker.email", is(bookerDto.getEmail())))
                .andExpect(jsonPath("$[0].status", is(bookingFullDto.getStatus().toString())));
    }

    @DisplayName("Вызов исключения ValidationException при получении списка бронирований по идентификатору владельца")
    @Test
    void getAllBookingsByOwnerWith400Exception() throws Exception {
        when(bookingService.findAllByOwnerAndState(anyLong(), anyString(), anyInt(), anyInt()))
                .thenThrow(IncorrectDataException.class);
        mockMvc.perform(get("/bookings/owner")
                        .header(X_SHARER_HEADER, bookerDto.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Вызов исключения NotFoundException при получении списка бронирований по идентификатору владельца")
    @Test
    void getAllBookingsByOwnerWith404Exception() throws Exception {
        when(bookingService.findAllByOwnerAndState(anyLong(), anyString(), anyInt(), anyInt()))
                .thenThrow(NotFoundException.class);
        mockMvc.perform(get("/bookings/owner")
                        .header(X_SHARER_HEADER, bookerDto.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isNotFound());
    }

    @DisplayName("Вызов исключения RuntimeException при получении списка бронирований по идентификатору владельца")
    @Test
    void getAllBookingsByOwnerWith500Exception() throws Exception {
        when(bookingService.findAllByOwnerAndState(anyLong(), anyString(), anyInt(), anyInt()))
                .thenThrow(RuntimeException.class);
        mockMvc.perform(get("/bookings/owner")
                        .header(X_SHARER_HEADER, bookerDto.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().is5xxServerError())
                .andExpect(status().isInternalServerError());
    }

    @DisplayName("Получение бронирования по идентификатору бронирования и бронирующего")
    @Test
    void getBookingByIdAndBookerId() throws Exception {
        when(bookingService.findByBookerIdAndBookingId(anyLong(), anyLong()))
                .thenReturn(bookingFullDto);

        mockMvc.perform(get("/bookings/" + bookingFullDto.getId())
                        .header(X_SHARER_HEADER, bookerDto.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingFullDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingFullDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingFullDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.item.id", is(itemShortDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(itemShortDto.getName())))
                .andExpect(jsonPath("$.item.sharer.id", is(ownerDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.sharer.name", is(ownerDto.getName())))
                .andExpect(jsonPath("$.item.sharer.email", is(ownerDto.getEmail())))
                .andExpect(jsonPath("$.booker.id", is(bookerDto.getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookerDto.getName())))
                .andExpect(jsonPath("$.booker.email", is(bookerDto.getEmail())))
                .andExpect(jsonPath("$.status", is(bookingFullDto.getStatus().toString())));
    }

    @DisplayName("Вызов исключения ValidationException при получении по идентификатору бронирования и бронирующего")
    @Test
    void getBookingByIdWith400Exception() throws Exception {
        when(bookingService.findByBookerIdAndBookingId(anyLong(), anyLong()))
                .thenThrow(IncorrectDataException.class);

        mockMvc.perform(get("/bookings/" + bookingFullDto.getId())
                        .header(X_SHARER_HEADER, bookerDto.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Вызов исключения UserIsNotSharerException при получении по идентификатору бронирования и бронирующего")
    @Test
    void getBookingByIdWith403Exception() throws Exception {
        when(bookingService.findByBookerIdAndBookingId(anyLong(), anyLong()))
                .thenThrow(UserIsNotSharerException.class);

        mockMvc.perform(get("/bookings/" + bookingFullDto.getId())
                        .header(X_SHARER_HEADER, bookerDto.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isForbidden());
    }

    @DisplayName("Вызов исключения NotFoundException при получении по идентификатору бронирования и бронирующего")
    @Test
    void getBookingByIdWith404Exception() throws Exception {
        when(bookingService.findByBookerIdAndBookingId(anyLong(), anyLong()))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(get("/bookings/" + bookingFullDto.getId())
                        .header(X_SHARER_HEADER, bookerDto.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isNotFound());
    }

    @DisplayName("Вызов исключения RuntimeException при получении по идентификатору бронирования и бронирующего")
    @Test
    void getBookingByIdWith500Exception() throws Exception {
        when(bookingService.findByBookerIdAndBookingId(anyLong(), anyLong()))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(get("/bookings/" + bookingFullDto.getId())
                        .header(X_SHARER_HEADER, bookerDto.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().is5xxServerError())
                .andExpect(status().isInternalServerError());
    }

    @DisplayName("Создание бронирования")
    @Test
    void createBooking() throws Exception {
        when(bookingService.create(anyLong(), any()))
                .thenReturn(bookingFullDto);

        mockMvc.perform(post("/bookings")
                        .header(X_SHARER_HEADER, bookerDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(bookingCreateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingFullDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingFullDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingFullDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.item.id", is(itemShortDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(itemShortDto.getName())))
                .andExpect(jsonPath("$.item.sharer.id", is(ownerDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.sharer.name", is(ownerDto.getName())))
                .andExpect(jsonPath("$.item.sharer.email", is(ownerDto.getEmail())))
                .andExpect(jsonPath("$.booker.id", is(bookerDto.getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookerDto.getName())))
                .andExpect(jsonPath("$.booker.email", is(bookerDto.getEmail())))
                .andExpect(jsonPath("$.status", is(bookingFullDto.getStatus().toString())));
    }

    @DisplayName("Вызов исключения ValidationException при создании бронирования")
    @Test
    void createBookingWith400Exception() throws Exception {
        when(bookingService.create(anyLong(), any()))
                .thenThrow(IncorrectDataException.class);

        mockMvc.perform(post("/bookings")
                        .header(X_SHARER_HEADER, ownerDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Вызов исключения UserIsNotSharerException при создании бронирования")
    @Test
    void createBookingWith403Exception() throws Exception {
        when(bookingService.create(anyLong(), any()))
                .thenThrow(UserIsNotSharerException.class);

        mockMvc.perform(post("/bookings")
                        .header(X_SHARER_HEADER, ownerDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isForbidden());
    }

    @DisplayName("Вызов исключения NotFoundException при создании бронирования")
    @Test
    void createBookingWith404Exception() throws Exception {
        when(bookingService.create(anyLong(), any()))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(post("/bookings")
                        .header(X_SHARER_HEADER, ownerDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isNotFound());
    }

    @DisplayName("Вызов исключения RuntimeException при создании бронирования")
    @Test
    void createBookingWith500Exception() throws Exception {
        when(bookingService.create(anyLong(), any()))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(post("/bookings")
                        .header(X_SHARER_HEADER, ownerDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andExpect(status().isInternalServerError());
    }

    @DisplayName("Изменение статуса бронирования")
    @Test
    public void approveBooking() throws Exception {
        bookingFullDto.setStatus(BookingStatus.APPROVED);

        when(bookingService.approve(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingFullDto);

        mockMvc.perform(patch("/bookings/" + bookingFullDto.getId() + "?approved=true")
                        .header(X_SHARER_HEADER, ownerDto.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingFullDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingFullDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingFullDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.item.id", is(itemShortDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(itemShortDto.getName())))
                .andExpect(jsonPath("$.item.sharer.id", is(ownerDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.sharer.name", is(ownerDto.getName())))
                .andExpect(jsonPath("$.item.sharer.email", is(ownerDto.getEmail())))
                .andExpect(jsonPath("$.booker.id", is(bookerDto.getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookerDto.getName())))
                .andExpect(jsonPath("$.booker.email", is(bookerDto.getEmail())))
                .andExpect(jsonPath("$.status", is(bookingFullDto.getStatus().toString())));
    }

    @DisplayName("Вызов исключения ValidationException при изменении статуса бронирования")
    @Test
    void approveBookingWith400Exception() throws Exception {
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(IncorrectDataException.class);

        mockMvc.perform(patch("/bookings/" + bookingFullDto.getId() + "?approved=true")
                        .header(X_SHARER_HEADER, ownerDto.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Вызов исключения UserIsNotSharerException при изменении статуса бронирования")
    @Test
    void approveBookingWith403Exception() throws Exception {
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(UserIsNotSharerException.class);

        mockMvc.perform(patch("/bookings/" + bookingFullDto.getId() + "?approved=true")
                        .header(X_SHARER_HEADER, ownerDto.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isForbidden());
    }

    @DisplayName("Вызов исключения NotFoundException при изменении статуса бронирования")
    @Test
    void approveBookingWith404Exception() throws Exception {
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(patch("/bookings/" + bookingFullDto.getId() + "?approved=true")
                        .header(X_SHARER_HEADER, ownerDto.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isNotFound());
    }

    @DisplayName("Вызов исключения RuntimeException при изменении статуса бронирования")
    @Test
    void approveBookingWith500Exception() throws Exception {
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(patch("/bookings/" + bookingFullDto.getId() + "?approved=true")
                        .header(X_SHARER_HEADER, ownerDto.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andExpect(status().isInternalServerError());
    }
}