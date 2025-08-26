package ru.practicum.shareit.item.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Проверка преобразования DTO вещей в JSON-объекты")
@JsonTest
class ItemDtoTest {

    @Autowired
    private JacksonTester<CommentCreateDto> commentCreateDtoJacksonTester;

    @Autowired
    private JacksonTester<CommentShortDto> commentShortDtoJacksonTester;

    @Autowired
    private JacksonTester<ItemCreateDto> itemCreateDtoJacksonTester;

    @Autowired
    private JacksonTester<ItemFullDto> itemFullDtoJacksonTester;

    @Autowired
    private JacksonTester<ItemShortDto> itemShortDtoJacksonTester;

    @Autowired
    private JacksonTester<ItemUpdateDto> itemUpdateDtoJacksonTester;

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

        owner = UserDto.builder().id(Math.abs(random.nextLong())).name("User Test").email("owner@system.com").build();

        booker = UserDto.builder().id(Math.abs(random.nextLong())).name("User Test").email("booker@system.com").build();

        lastBookingShortDto = BookingShortDto.builder().id(Math.abs(random.nextLong())).bookerId(booker.getId())
                .start(startDate).end(endDate).build();

        nextBookingShortDto = BookingShortDto.builder().id(Math.abs(random.nextLong())).bookerId(booker.getId())
                .start(lastBookingShortDto.getStart().plusDays(3)).end(lastBookingShortDto.getEnd().plusDays(3))
                .build();

        commentShortDto = CommentShortDto.builder().id(Math.abs(random.nextLong())).text("Comment text")
                .authorName(booker.getName()).created(lastBookingShortDto.getEnd().plusHours(1)).build();

        itemRequestShortDto = ItemRequestShortDto.builder().id(Math.abs(random.nextLong())).requestor(booker)
                .description("Item request description").created(LocalDateTime.now().minusDays(3)).build();

        itemShortDto = ItemShortDto.builder().id(Math.abs(random.nextLong())).name("Item name")
                .description("Item description").sharer(owner).available(true).request(itemRequestShortDto).build();

        itemRequestFullDto = ItemRequestFullDto.builder().id(itemRequestShortDto.getId())
                .description(itemRequestShortDto.getDescription()).created(itemRequestShortDto.getCreated())
                .items(List.of(itemShortDto)).requestor(itemRequestShortDto.getRequestor()).build();

        itemFullDto = ItemFullDto.builder().id(itemShortDto.getId()).sharer(itemShortDto.getSharer())
                .name(itemShortDto.getName()).description(itemShortDto.getDescription())
                .available(itemShortDto.getAvailable()).lastBooking(lastBookingShortDto)
                .nextBooking(nextBookingShortDto).comments(List.of(commentShortDto)).request(itemRequestFullDto)
                .build();

        itemCreateDto = ItemCreateDto.builder().name(itemShortDto.getName()).description(itemShortDto.getDescription())
                .available(itemShortDto.getAvailable()).requestId(itemRequestShortDto.getId()).build();

        commentCreateDto = CommentCreateDto.builder().text(commentShortDto.getText()).authorId(booker.getId())
                .created(commentShortDto.getCreated()).build();

        itemUpdateDto = ItemUpdateDto.builder().itemId(itemFullDto.getId()).name(itemFullDto.getName())
                .description(itemFullDto.getDescription()).available(itemFullDto.getAvailable()).build();
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

    @DisplayName("Проверка преобразования в JSON-объект для класса CommentCreateDto")
    @Test
    void commentCreateDtoJacksonTesterTest() throws Exception {
        JsonContent<CommentCreateDto> jsonContent = commentCreateDtoJacksonTester.write(commentCreateDto);

        assertThat(jsonContent).hasJsonPath("$.text");
        assertThat(jsonContent).extractingJsonPathStringValue("$.text").isEqualTo(commentCreateDto.getText());

        assertThat(jsonContent).hasJsonPath("$.authorId");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.authorId").isEqualTo(commentCreateDto.getAuthorId());

        assertThat(jsonContent).hasJsonPath("$.created");
        assertThat(jsonContent).extractingJsonPathStringValue("$.created")
                .isEqualTo(commentCreateDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME));
    }

    @DisplayName("Проверка преобразования в JSON-объект для класса CommentCreateDto")
    @Test
    void commentShortDtoJacksonTesterTest() throws Exception {
        JsonContent<CommentShortDto> jsonContent = commentShortDtoJacksonTester.write(commentShortDto);

        assertThat(jsonContent).hasJsonPath("$.id");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(commentShortDto.getId());

        assertThat(jsonContent).hasJsonPath("$.text");
        assertThat(jsonContent).extractingJsonPathStringValue("$.text").isEqualTo(commentShortDto.getText());

        assertThat(jsonContent).hasJsonPath("$.authorName");
        assertThat(jsonContent).extractingJsonPathStringValue("$.authorName")
                .isEqualTo(commentShortDto.getAuthorName());

        assertThat(jsonContent).hasJsonPath("$.created");
        assertThat(jsonContent).extractingJsonPathStringValue("$.created")
                .isEqualTo(commentShortDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME));
    }

    @DisplayName("Проверка преобразования в JSON-объект для класса ItemCreateDto")
    @Test
    void itemCreateDtoJacksonTesterTest() throws Exception {
        JsonContent<ItemCreateDto> jsonContent = itemCreateDtoJacksonTester.write(itemCreateDto);

        assertThat(jsonContent).hasJsonPath("$.name");
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo(itemCreateDto.getName());

        assertThat(jsonContent).hasJsonPath("$.description");
        assertThat(jsonContent).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemCreateDto.getDescription());

        assertThat(jsonContent).hasJsonPath("$.available");
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.available").isEqualTo(itemCreateDto.getAvailable());

        assertThat(jsonContent).hasJsonPath("$.requestId");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.requestId").isEqualTo(itemCreateDto.getRequestId());
    }

    @DisplayName("Проверка преобразования в JSON-объект для класса ItemFullDto")
    @Test
    void itemFullDtoJacksonTesterTest() throws Exception {
        JsonContent<ItemFullDto> jsonContent = itemFullDtoJacksonTester.write(itemFullDto);

        assertThat(jsonContent).hasJsonPath("$.id");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(itemFullDto.getId());

        assertThat(jsonContent).hasJsonPath("$.name");
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo(itemFullDto.getName());

        assertThat(jsonContent).hasJsonPath("$.description");
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo(itemFullDto.getDescription());

        assertThat(jsonContent).hasJsonPath("$.available");
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.available").isEqualTo(itemFullDto.getAvailable());

        assertThat(jsonContent).hasJsonPath("$.sharer");
        assertThat(jsonContent).hasJsonPath("$.sharer.id");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.sharer.id")
                .isEqualTo(owner.getId());

        assertThat(jsonContent).hasJsonPath("$.sharer.name");
        assertThat(jsonContent).extractingJsonPathStringValue("$.sharer.name")
                .isEqualTo(owner.getName());

        assertThat(jsonContent).hasJsonPath("$.sharer.email");
        assertThat(jsonContent).extractingJsonPathStringValue("$.sharer.email")
                .isEqualTo(owner.getEmail());

        assertThat(jsonContent).hasJsonPath("$.lastBooking");
        assertThat(jsonContent).hasJsonPath("$.lastBooking.id");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.lastBooking.id")
                .isEqualTo(lastBookingShortDto.getId());

        assertThat(jsonContent).hasJsonPath("$.lastBooking.bookerId");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.lastBooking.bookerId")
                .isEqualTo(lastBookingShortDto.getBookerId());

        assertThat(jsonContent).hasJsonPath("$.lastBooking.start");
        assertThat(jsonContent).extractingJsonPathStringValue("$.lastBooking.start")
                .isEqualTo(lastBookingShortDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME));

        assertThat(jsonContent).hasJsonPath("$.lastBooking.end");
        assertThat(jsonContent).extractingJsonPathStringValue("$.lastBooking.end")
                .isEqualTo(lastBookingShortDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME));

        assertThat(jsonContent).hasJsonPath("$.nextBooking");
        assertThat(jsonContent).hasJsonPath("$.lastBooking.id");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.nextBooking.id")
                .isEqualTo(nextBookingShortDto.getId());

        assertThat(jsonContent).hasJsonPath("$.nextBooking.bookerId");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.nextBooking.bookerId")
                .isEqualTo(nextBookingShortDto.getBookerId());

        assertThat(jsonContent).hasJsonPath("$.nextBooking.start");
        assertThat(jsonContent).extractingJsonPathStringValue("$.nextBooking.start")
                .isEqualTo(nextBookingShortDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME));

        assertThat(jsonContent).hasJsonPath("$.nextBooking.end");
        assertThat(jsonContent).extractingJsonPathStringValue("$.nextBooking.end")
                .isEqualTo(nextBookingShortDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME));

        assertThat(jsonContent).hasJsonPath("$.request");
        assertThat(jsonContent).hasJsonPath("$.request.id");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.request.id")
                .isEqualTo(itemRequestFullDto.getId());

        assertThat(jsonContent).hasJsonPath("$.request.description");
        assertThat(jsonContent).extractingJsonPathStringValue("$.request.description")
                .isEqualTo(itemRequestFullDto.getDescription());

        assertThat(jsonContent).hasJsonPath("$.request.created");
        assertThat(jsonContent).extractingJsonPathStringValue("$.request.created")
                .isEqualTo(itemRequestFullDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME));

        assertThat(jsonContent).hasJsonPath("$.request.requestor");
        assertThat(jsonContent).hasJsonPath("$.request.requestor.id");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.request.requestor.id")
                .isEqualTo(booker.getId());

        assertThat(jsonContent).hasJsonPath("$.request.requestor.name");
        assertThat(jsonContent).extractingJsonPathStringValue("$.request.requestor.name")
                .isEqualTo(booker.getName());

        assertThat(jsonContent).hasJsonPath("$.request.requestor.email");
        assertThat(jsonContent).extractingJsonPathStringValue("$.request.requestor.email")
                .isEqualTo(booker.getEmail());
    }

    @DisplayName("Проверка преобразования в JSON-объект для класса ItemShortDto")
    @Test
    void itemShortDtoJacksonTesterTest() throws Exception {
        JsonContent<ItemShortDto> jsonContent = itemShortDtoJacksonTester.write(itemShortDto);

        assertThat(jsonContent).hasJsonPath("$.id");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(itemShortDto.getId());

        assertThat(jsonContent).hasJsonPath("$.name");
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo(itemShortDto.getName());

        assertThat(jsonContent).hasJsonPath("$.description");
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo(itemShortDto.getDescription());

        assertThat(jsonContent).hasJsonPath("$.available");
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.available").isEqualTo(itemShortDto.getAvailable());

        assertThat(jsonContent).hasJsonPath("$.sharer");
        assertThat(jsonContent).hasJsonPath("$.sharer.id");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.sharer.id")
                .isEqualTo(owner.getId());

        assertThat(jsonContent).hasJsonPath("$.sharer.name");
        assertThat(jsonContent).extractingJsonPathStringValue("$.sharer.name")
                .isEqualTo(owner.getName());

        assertThat(jsonContent).hasJsonPath("$.sharer.email");
        assertThat(jsonContent).extractingJsonPathStringValue("$.sharer.email")
                .isEqualTo(owner.getEmail());

        assertThat(jsonContent).hasJsonPath("$.request");
        assertThat(jsonContent).hasJsonPath("$.request.id");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.request.id")
                .isEqualTo(itemRequestShortDto.getId());

        assertThat(jsonContent).hasJsonPath("$.request.description");
        assertThat(jsonContent).extractingJsonPathStringValue("$.request.description")
                .isEqualTo(itemRequestShortDto.getDescription());

        assertThat(jsonContent).hasJsonPath("$.request.created");
        assertThat(jsonContent).extractingJsonPathStringValue("$.request.created")
                .isEqualTo(itemRequestShortDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME));

        assertThat(jsonContent).hasJsonPath("$.request.requestor");
        assertThat(jsonContent).hasJsonPath("$.request.requestor.id");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.request.requestor.id")
                .isEqualTo(booker.getId());

        assertThat(jsonContent).hasJsonPath("$.request.requestor.name");
        assertThat(jsonContent).extractingJsonPathStringValue("$.request.requestor.name")
                .isEqualTo(booker.getName());

        assertThat(jsonContent).hasJsonPath("$.request.requestor.email");
        assertThat(jsonContent).extractingJsonPathStringValue("$.request.requestor.email")
                .isEqualTo(booker.getEmail());
    }

    @DisplayName("Проверка преобразования в JSON-объект для класса ItemUpdateDto")
    @Test
    void itemUpdateDtoJacksonTesterTest() throws Exception {
        JsonContent<ItemUpdateDto> jsonContent = itemUpdateDtoJacksonTester.write(itemUpdateDto);

        assertThat(jsonContent).hasJsonPath("$.itemId");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(itemUpdateDto.getItemId());

        assertThat(jsonContent).hasJsonPath("$.name");
        assertThat(jsonContent).extractingJsonPathStringValue("$.name")
                .isEqualTo(itemUpdateDto.getName());

        assertThat(jsonContent).hasJsonPath("$.description");
        assertThat(jsonContent).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemUpdateDto.getDescription());

        assertThat(jsonContent).hasJsonPath("$.available");
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemUpdateDto.getAvailable());
    }
}