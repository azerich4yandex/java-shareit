package ru.practicum.shareit.booking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

/**
 * Бронирование.
 */
@Table(name = "bookings")
@Entity
@Builder
@EqualsAndHashCode(of = "entityId")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Booking {

    /**
     * Идентификатор сущности
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long entityId;

    /**
     * Дата начала бронирования
     */
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    /**
     * Дата окончания бронирования
     */
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    /**
     * Бронируемая вещь
     */
    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item;

    /**
     * Арендатор
     */
    @ManyToOne
    @JoinColumn(name = "booker_id", referencedColumnName = "id")
    private User booker;

    /**
     * Статус бронирования
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookingStatus status;
}
