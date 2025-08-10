package ru.practicum.shareit.item.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.user.dto.UserDto;

@Data
@Builder
public class ItemFullDto {

    private Long id;
    private UserDto sharer;
    private String name;
    private String description;
    private Boolean available;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private List<CommentShortDto> comments;
}
