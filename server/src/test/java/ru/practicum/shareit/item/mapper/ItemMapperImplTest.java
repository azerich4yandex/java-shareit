package ru.practicum.shareit.item.mapper;

import java.util.Random;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Проверка работы маппера ItemMapper")
class ItemMapperImplTest {

    private ItemMapperImpl itemMapper;

    @BeforeEach
    void init() {
        itemMapper = new ItemMapperImpl();
    }

    @AfterEach
    void halt() {
        itemMapper = null;
    }

    @DisplayName("Проверка преобразования из ItemCreateDto в Item")
    @Test
    void mapToItem() {
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("Item test")
                .description("Item description")
                .available(true)
                .build();

        Item item = itemMapper.mapToItem(itemCreateDto);
        assertNotNull(item);
        assertEquals(itemCreateDto.getName(), item.getName());
        assertEquals(itemCreateDto.getDescription(), item.getDescription());
        assertEquals(itemCreateDto.getAvailable(), item.getAvailable());
    }

    @DisplayName("Проверка преобразования из Item в ItemShortDto")
    @Test
    void mapToShortDto() {
        Random random = new Random();

        Item item = Item.builder()
                .entityId(Math.abs(random.nextLong()))
                .name("Item test")
                .description("Item description")
                .available(true)
                .build();

        ItemShortDto itemShortDto = itemMapper.mapToShortDto(item);
        assertNotNull(itemShortDto);
        assertNotNull(itemShortDto.getId());
        assertEquals(item.getEntityId(), itemShortDto.getId());
        assertEquals(item.getName(), itemShortDto.getName());
        assertEquals(item.getDescription(), itemShortDto.getDescription());
        assertEquals(item.getAvailable(), itemShortDto.getAvailable());
    }

    @DisplayName("Проверка преобразования из Item в ItemFullDto")
    @Test
    void mapToFullDto() {
        Random random = new Random();

        Item item = Item.builder()
                .entityId(Math.abs(random.nextLong()))
                .name("Item test")
                .description("Item description")
                .available(true)
                .build();

        ItemFullDto itemFullDto = itemMapper.mapToFullDto(item);
        assertNotNull(itemFullDto);
        assertNotNull(itemFullDto.getId());
        assertEquals(item.getEntityId(), itemFullDto.getId());
        assertEquals(item.getName(), itemFullDto.getName());
        assertEquals(item.getDescription(), itemFullDto.getDescription());
        assertEquals(item.getAvailable(), itemFullDto.getAvailable());
    }

    @DisplayName("Проверка преобразования из ItemUpdateDto в Item")
    @Test
    void updateItemFields() {
        Random random = new Random();

        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .itemId(Math.abs(random.nextLong()))
                .name("Item test")
                .description("Item description")
                .available(true)
                .build();

        Item item = new Item();

        itemMapper.updateItemFields(itemUpdateDto, item);
        assertNotNull(item);
        assertEquals(itemUpdateDto.getName(), item.getName());
        assertEquals(itemUpdateDto.getDescription(), item.getDescription());
        assertEquals(itemUpdateDto.getAvailable(), item.getAvailable());
    }
}