package ru.practicum.shareit.request.dto;

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
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Проверка преобразования DTO запросов в JSON-объекты")
@JsonTest
class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestCreateDto> itemRequestCreateDtoJacksonTester;

    @Autowired
    private JacksonTester<ItemRequestFullDto> itemRequestFullDtoJacksonTester;

    @Autowired
    private JacksonTester<ItemRequestShortDto> itemRequestShortDtoJacksonTester;

    private UserDto requestor;
    private UserDto owner;
    private ItemRequestShortDto itemRequestShortDto;
    private ItemShortDto itemShortDto;
    private ItemRequestFullDto itemRequestFullDto;
    private ItemRequestCreateDto itemRequestCreateDto;

    @BeforeEach
    void setUp() {
        Random random = new Random();

        requestor = UserDto.builder()
                .id(Math.abs(random.nextLong()))
                .name("User Test")
                .email("requestor@system.com")
                .build();

        owner = UserDto.builder()
                .id(Math.abs(random.nextLong()))
                .name("User Test")
                .email("owner@system.com")
                .build();

        itemRequestShortDto = ItemRequestShortDto.builder()
                .id(Math.abs(random.nextLong()))
                .requestor(requestor)
                .description("Request description")
                .created(LocalDateTime.now().minusDays(1))
                .build();

        itemShortDto = ItemShortDto.builder()
                .id(Math.abs(random.nextLong()))
                .sharer(owner)
                .name("Item name")
                .description("Item description")
                .available(true)
                .request(itemRequestShortDto)
                .build();

        itemRequestFullDto = ItemRequestFullDto.builder()
                .id(itemRequestShortDto.getId())
                .description(itemRequestShortDto.getDescription())
                .created(itemRequestShortDto.getCreated())
                .requestor(itemRequestShortDto.getRequestor())
                .items(List.of(itemShortDto))
                .build();

        itemRequestCreateDto = ItemRequestCreateDto.builder()
                .description(itemRequestShortDto.getDescription())
                .build();
    }

    @AfterEach
    void tearDown() {
        requestor = null;
        owner = null;
        itemRequestShortDto = null;
        itemShortDto = null;
        itemRequestFullDto = null;
        itemRequestCreateDto = null;
    }

    @DisplayName("Проверка преобразования в JSON-объект для класса ItemRequestCreateDto")
    @Test
    void itemRequestCreateDtoJacksonTesterTest() throws Exception {
        JsonContent<ItemRequestCreateDto> jsonContent = itemRequestCreateDtoJacksonTester.write(itemRequestCreateDto);

        assertThat(jsonContent).hasJsonPath("$.description");
        assertThat(jsonContent).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequestCreateDto.getDescription());

    }

    @DisplayName("Проверка преобразования в JSON-объект для класса ItemRequestFullDto")
    @Test
    void itemRequestFullDtoJacksonTesterTest() throws Exception {
        JsonContent<ItemRequestFullDto> jsonContent = itemRequestFullDtoJacksonTester.write(itemRequestFullDto);

        assertThat(jsonContent).hasJsonPath("$.id");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemRequestFullDto.getId());

        assertThat(jsonContent).hasJsonPath("$.description");
        assertThat(jsonContent).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequestFullDto.getDescription());
        assertThat(jsonContent).hasJsonPath("$.requestor");
        assertThat(jsonContent).hasJsonPath("$.requestor.id");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.requestor.id")
                .isEqualTo(requestor.getId());

        assertThat(jsonContent).hasJsonPath("$.requestor.name");
        assertThat(jsonContent).extractingJsonPathStringValue("$.requestor.name")
                .isEqualTo(requestor.getName());
        assertThat(jsonContent).hasJsonPath("$.requestor.email");
        assertThat(jsonContent).extractingJsonPathStringValue("$.requestor.email")
                .isEqualTo(requestor.getEmail());
        assertThat(jsonContent).hasJsonPath("$.created");
        assertThat(jsonContent).extractingJsonPathStringValue("$.created")
                .isEqualTo(itemRequestFullDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME));
        assertThat(jsonContent).hasJsonPath("$.items");
        assertThat(jsonContent).hasJsonPath("$.items[0].id");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.items[0].id")
                .isEqualTo(itemShortDto.getId());

        assertThat(jsonContent).hasJsonPath("$.items[0].sharer");
        assertThat(jsonContent).hasJsonPath("$.items[0].sharer.id");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.items[0].sharer.id")
                .isEqualTo(owner.getId());

        assertThat(jsonContent).hasJsonPath("$.items[0].sharer.name");
        assertThat(jsonContent).extractingJsonPathStringValue("$.items[0].sharer.name")
                .isEqualTo(owner.getName());
        assertThat(jsonContent).hasJsonPath("$.items[0].sharer.email");
        assertThat(jsonContent).extractingJsonPathStringValue("$.items[0].sharer.email")
                .isEqualTo(owner.getEmail());
        assertThat(jsonContent).hasJsonPath("$.items[0].name");
        assertThat(jsonContent).extractingJsonPathStringValue("$.items[0].name")
                .isEqualTo(itemShortDto.getName());
        assertThat(jsonContent).hasJsonPath("$.items[0].description");
        assertThat(jsonContent).extractingJsonPathStringValue("$.items[0].description")
                .isEqualTo(itemShortDto.getDescription());
        assertThat(jsonContent).hasJsonPath("$.items[0].available");
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.items[0].available")
                .isEqualTo(itemShortDto.getAvailable());
        assertThat(jsonContent).hasJsonPath("$.items[0].request");
        assertThat(jsonContent).hasJsonPath("$.items[0].request.id");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.items[0].request.id")
                .isEqualTo(itemRequestShortDto.getId());

        assertThat(jsonContent).hasJsonPath("$.items[0].request.requestor");
        assertThat(jsonContent).hasJsonPath("$.items[0].request.requestor.id");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.items[0].request.requestor.id")
                .isEqualTo(requestor.getId());

        assertThat(jsonContent).hasJsonPath("$.items[0].request.requestor.name");
        assertThat(jsonContent).extractingJsonPathStringValue("$.items[0].request.requestor.name")
                .isEqualTo(requestor.getName());
        assertThat(jsonContent).hasJsonPath("$.items[0].request.requestor.email");
        assertThat(jsonContent).extractingJsonPathStringValue("$.items[0].request.requestor.email")
                .isEqualTo(requestor.getEmail());
        assertThat(jsonContent).hasJsonPath("$.items[0].request.description");
        assertThat(jsonContent).extractingJsonPathStringValue("$.items[0].request.description")
                .isEqualTo(itemRequestShortDto.getDescription());
        assertThat(jsonContent).hasJsonPath("$.items[0].request.created");
        assertThat(jsonContent).extractingJsonPathStringValue("$.items[0].request.created")
                .isEqualTo(itemRequestShortDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME));
    }

    @DisplayName("Проверка преобразования в JSON-объект для класса ItemRequestShortDto")
    @Test
    void itemRequestShortDtoJacksonTesterTest() throws Exception {
        JsonContent<ItemRequestShortDto> jsonContent = itemRequestShortDtoJacksonTester.write(itemRequestShortDto);

        assertThat(jsonContent).hasJsonPath("$.id");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemRequestShortDto.getId());
        assertThat(jsonContent).hasJsonPath("$.requestor");
        assertThat(jsonContent).hasJsonPath("$.requestor.id");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.requestor.id")
                .isEqualTo(requestor.getId());
        assertThat(jsonContent).hasJsonPath("$.requestor.name");
        assertThat(jsonContent).extractingJsonPathStringValue("$.requestor.name")
                .isEqualTo(requestor.getName());
        assertThat(jsonContent).hasJsonPath("$.requestor.email");
        assertThat(jsonContent).extractingJsonPathStringValue("$.requestor.email")
                .isEqualTo(requestor.getEmail());
        assertThat(jsonContent).hasJsonPath("$.description");
        assertThat(jsonContent).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequestShortDto.getDescription());
        assertThat(jsonContent).hasJsonPath("$.created");
        assertThat(jsonContent).extractingJsonPathStringValue("$.created")
                .isEqualTo(itemRequestShortDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME));
    }
}