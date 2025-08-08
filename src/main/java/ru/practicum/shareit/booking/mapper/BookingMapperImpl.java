package ru.practicum.shareit.booking.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

@Service
@Slf4j
public class BookingMapperImpl implements BookingMapper {

    @Override
    public BookingFullDto mapToFullDto(Booking booking) {
        log.debug("Преобразование данных из модели  {} в полную модель {}", Booking.class, BookingFullDto.class);
        return BookingFullDto.builder()
                .id(booking.getEntityId())
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .status(booking.getStatus())
                .build();
    }

    @Override
    public BookingShortDto mapToShortDto(Booking booking) {
        log.debug("Преобразование данных из модели {} в краткую модель {}", Booking.class, BookingFullDto.class);
        return BookingShortDto.builder()
                .id(booking.getEntityId())
                .bookerId(booking.getBooker().getEntityId())
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .build();
    }

    @Override
    public Booking mapToBooking(BookingCreateDto dto) {
        log.debug("Преобразование данных из модели {} в модель {} для сохранения", BookingCreateDto.class,
                Booking.class);
        return Booking.builder()
                .startDate(dto.getStart())
                .endDate(dto.getEnd())
                .status(BookingStatus.WAITING)
                .build();
    }
}
