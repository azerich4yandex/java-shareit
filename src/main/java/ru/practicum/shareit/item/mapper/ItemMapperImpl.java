package ru.practicum.shareit.item.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

@Service
@Slf4j
public class ItemMapperImpl implements ItemMapper {

    @Override
    public Item mapToItem(ItemCreateDto dto) {
        log.debug("Преобразование данных из модели {} в модель {} для сохранения", ItemCreateDto.class,
                Item.class);
        return Item.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .build();
    }

    @Override
    public ItemShortDto mapToShortDto(Item item) {
        log.debug("Преобразование данных из модели {} в краткую модель {} для сохранения", Item.class,
                ItemShortDto.class);
        return ItemShortDto.builder()
                .id(item.getEntityId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    @Override
    public ItemFullDto mapToFullDto(Item item) {
        log.debug("Преобразование данных из модели {} в полную модель {} для сохранения", Item.class,
                ItemFullDto.class);
        return ItemFullDto.builder()
                .id(item.getEntityId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    @Override
    public void updateItemFields(ItemUpdateDto dto, Item item) {
        log.debug("Изменение полей в экземпляре класса {} на основе данных из экземпляра класса {}", Item.class,
                ItemUpdateDto.class);
        if (dto.hasName()) {
            log.debug("Будет изменено наименование");
            item.setName(dto.getName());
        }

        if (dto.hasDescription()) {
            log.debug("Будет изменено описание");
            item.setDescription(dto.getDescription());
        }

        if (dto.hasAvailable()) {
            log.debug("Будет изменен признак доступности");
            item.setAvailable(dto.getAvailable());
        }

    }
}
