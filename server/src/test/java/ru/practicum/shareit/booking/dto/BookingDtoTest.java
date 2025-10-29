package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Проверка преобразования DTO бронирований в JSON-объекты")
@JsonTest
class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingCreateDto> bookingCreateDtoJacksonTester;

    @Autowired
    private JacksonTester<BookingFullDto> bookingFullDtoJacksonTester;

    @Autowired
    private JacksonTester<BookingShortDto> bookingShortDtoJacksonTester;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private UserDto ownerDto;
    private UserDto bookerDto;
    private ItemShortDto itemShortDto;

    private BookingCreateDto bookingCreateDto;
    private BookingFullDto bookingFullDto;
    private BookingShortDto bookingShortDto;


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
                .name("First item")
                .description("First item description")
                .available(true)
                .sharer(ownerDto)
                .build();

        bookingCreateDto = BookingCreateDto.builder()
                .start(startDate)
                .end(endDate)
                .itemId(itemShortDto.getId())
                .bookerId(bookerDto.getId())
                .build();

        bookingFullDto = BookingFullDto.builder()
                .id(Math.abs(random.nextLong()))
                .start(bookingCreateDto.getStart())
                .end(bookingCreateDto.getEnd())
                .item(itemShortDto)
                .booker(bookerDto)
                .status(BookingStatus.WAITING)
                .build();

        bookingShortDto = BookingShortDto.builder()
                .id(bookingFullDto.getId())
                .bookerId(bookerDto.getId())
                .start(bookingCreateDto.getStart())
                .end(bookingCreateDto.getEnd())
                .build();
    }

    @AfterEach
    void tearDown() {
        startDate = null;
        endDate = null;

        ownerDto = null;
        bookerDto = null;

        itemShortDto = null;

        bookingCreateDto = null;
        bookingFullDto = null;
        bookingShortDto = null;
    }

    @DisplayName("Проверка преобразования в JSON-объект для класса BookingCreateDto")
    @Test
    void bookingCreateDtoJacksonTesterTest() throws Exception {
        JsonContent<BookingCreateDto> jsonContent = bookingCreateDtoJacksonTester.write(bookingCreateDto);

        assertThat(jsonContent).hasJsonPath("$.start");
        assertThat(jsonContent).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingCreateDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME));

        assertThat(jsonContent).hasJsonPath("$.end");
        assertThat(jsonContent).extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingCreateDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME));

        assertThat(jsonContent).hasJsonPath("$.itemId");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(bookingCreateDto.getItemId());

        assertThat(jsonContent).hasJsonPath("$.bookerId");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.bookerId")
                .isEqualTo(bookingCreateDto.getBookerId());
    }

    @DisplayName("Проверка преобразования в JSON-объект для класса BookingFullDto")
    @Test
    void bookingFullDtoJacksonTesterTest() throws Exception {
        JsonContent<BookingFullDto> jsonContent = bookingFullDtoJacksonTester.write(bookingFullDto);

        assertThat(jsonContent).hasJsonPath("$.id");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.id")
                .isEqualTo(bookingFullDto.getId());

        assertThat(jsonContent).hasJsonPath("$.start");
        assertThat(jsonContent).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingFullDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME));

        assertThat(jsonContent).hasJsonPath("$.end");
        assertThat(jsonContent).extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingFullDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME));

        assertThat(jsonContent).hasJsonPath("$.status");
        assertThat(jsonContent).extractingJsonPathStringValue("$.status")
                .isEqualTo(bookingFullDto.getStatus().name());

        assertThat(jsonContent).hasJsonPath("$.item");
        assertThat(jsonContent).hasJsonPath("$.item.id");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.item.id")
                .isEqualTo(itemShortDto.getId());

        assertThat(jsonContent).hasJsonPath("$.item.name");
        assertThat(jsonContent).extractingJsonPathStringValue("$.item.name")
                .isEqualTo(itemShortDto.getName());

        assertThat(jsonContent).hasJsonPath("$.item.description");
        assertThat(jsonContent).extractingJsonPathStringValue("$.item.description")
                .isEqualTo(itemShortDto.getDescription());

        assertThat(jsonContent).hasJsonPath("$.item.available");
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.item.available")
                .isEqualTo(itemShortDto.getAvailable());

        assertThat(jsonContent).hasJsonPath("$.item.sharer");
        assertThat(jsonContent).hasJsonPath("$.item.sharer.id");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.item.sharer.id")
                .isEqualTo(ownerDto.getId());

        assertThat(jsonContent).hasJsonPath("$.item.sharer.name");
        assertThat(jsonContent).extractingJsonPathStringValue("$.item.sharer.name")
                .isEqualTo(ownerDto.getName());

        assertThat(jsonContent).hasJsonPath("$.item.sharer.email");
        assertThat(jsonContent).extractingJsonPathStringValue("$.item.sharer.email")
                .isEqualTo(ownerDto.getEmail());
    }

    @DisplayName("Проверка преобразования в JSON-объект для класса BookingShortDto")
    @Test
    void bookingShortDtoJacksonTesterTest() throws Exception {
        JsonContent<BookingShortDto> jsonContent = bookingShortDtoJacksonTester.write(bookingShortDto);

        assertThat(jsonContent).hasJsonPath("$.id");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.id")
                .isEqualTo(bookingShortDto.getId());

        assertThat(jsonContent).hasJsonPath("$.bookerId");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.bookerId")
                .isEqualTo(bookerDto.getId());

        assertThat(jsonContent).hasJsonPath("$.start");
        assertThat(jsonContent).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingShortDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME));

        assertThat(jsonContent).hasJsonPath("$.end");
        assertThat(jsonContent).extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingShortDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME));
    }
}