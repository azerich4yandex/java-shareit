package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public final class ItemMapper {

    public static Item mapToItem(NewItemDto dto) {
        return Item.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .build();
    }

    public static ItemDto mapToItemDto(Item item) {
        return ItemDto.builder()
                .itemId(item.getEntityId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static void updateItemFields(UpdateItemDto dto, Item item) {
        if (dto.hasName()) {
            item.setName(dto.getName());
        }

        if (dto.hasDescription()) {
            item.setDescription(dto.getDescription());
        }

        if (dto.hasAvailable()) {
            item.setAvailable(dto.getAvailable());
        }

    }
}
