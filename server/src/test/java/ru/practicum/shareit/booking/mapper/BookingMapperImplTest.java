package ru.practicum.shareit.booking.mapper;

import java.time.LocalDateTime;
import java.util.Random;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Проверка работы маппера BookingMapper")
class BookingMapperImplTest {

    private BookingMapperImpl bookingMapper;

    @BeforeEach
    void setUp() {
        bookingMapper = new BookingMapperImpl();
    }

    @AfterEach
    void tearDown() {
        bookingMapper = null;
    }

    @DisplayName("Проверка преобразования из Booking в BookingFullDto")
    @Test
    void mapToFullDto() {
        Random random = new Random();

        Booking booking = Booking.builder()
                .entityId(Math.abs(random.nextLong()))
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();

        BookingFullDto bookingFullDto = bookingMapper.mapToFullDto(booking);

        assertNotNull(bookingFullDto);
        assertNotNull(bookingFullDto.getId());
        assertEquals(booking.getEntityId(), bookingFullDto.getId());
        assertEquals(booking.getStartDate(), bookingFullDto.getStart());
        assertEquals(booking.getEndDate(), bookingFullDto.getEnd());
        assertEquals(booking.getStatus(), bookingFullDto.getStatus());
    }

    @DisplayName("Проверка преобразования из Booking в BookingShortDto")
    @Test
    void mapToShortDto() {
        Random random = new Random();

        User booker = User.builder()
                .entityId(Math.abs(random.nextLong()))
                .name("User Test")
                .email("user@system.com")
                .build();

        Booking booking = Booking.builder()
                .entityId(Math.abs(random.nextLong()))
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(booker)
                .build();

        BookingShortDto bookingShortDto = bookingMapper.mapToShortDto(booking);
        assertNotNull(bookingShortDto);
        assertNotNull(bookingShortDto.getId());
        assertEquals(booking.getEntityId(), bookingShortDto.getId());
        assertEquals(booking.getBooker().getEntityId(), bookingShortDto.getBookerId());
        assertEquals(booking.getStartDate(), bookingShortDto.getStart());
        assertEquals(booking.getEndDate(), bookingShortDto.getEnd());
    }

    @DisplayName("Проверка преобразования из BookingCreateDto в Booking")
    @Test
    void mapToBooking() {
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        Booking booking = bookingMapper.mapToBooking(bookingCreateDto);
        assertNotNull(booking);
        assertEquals(bookingCreateDto.getStart(), booking.getStartDate());
        assertEquals(bookingCreateDto.getEnd(), booking.getEndDate());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }
}