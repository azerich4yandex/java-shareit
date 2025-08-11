package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

@Data
@Builder
public class BookingCreateDto {

    @NotNull(message = "Дата начала бронирования должна быть заполнена")
    @FutureOrPresent(message = "Дата начала бронирования не может быть в прошлом")
    private LocalDateTime start;

    @NotNull(message = "Дата окончания бронирования должна быть заполнена")
    @Future(message = "Дата окончания бронирования должна быть в будущем")
    private LocalDateTime end;

    @NotNull(message = "Идентификатор бронируемой вещи должен быть заполнен")
    private Long itemId;

    private Long bookerId;
    private BookingStatus status;
}
