package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

@Service
public class ItemMapperImpl implements ItemMapper {

    @Override
    public Item mapToItem(ItemCreateDto dto) {
        return Item.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .build();
    }

    @Override
    public ItemDto mapToItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getEntityId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    @Override
    public void updateItemFields(ItemUpdateDto dto, Item item) {
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
