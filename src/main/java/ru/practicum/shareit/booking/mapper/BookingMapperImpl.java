package ru.practicum.shareit.booking.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

@Service
@Slf4j
public class BookingMapperImpl implements BookingMapper {

    @Override
    public BookingDto mapToBookingDto(Booking booking) {
        log.debug("Преобразование данных из модели  {} в модель {}", Booking.class, BookingDto.class);
        return BookingDto.builder()
                .id(booking.getEntityId())
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .status(booking.getStatus())
                .build();
    }

    @Override
    public Booking mapToBooking(BookingCreateDto dto) {
        log.debug("Преобразование данных из модели  {} в модель {} для сохранения", BookingCreateDto.class,
                Booking.class);
        return Booking.builder()
                .startDate(dto.getStart())
                .endDate(dto.getEnd())
                .status(BookingStatus.WAITING)
                .build();
    }
}
