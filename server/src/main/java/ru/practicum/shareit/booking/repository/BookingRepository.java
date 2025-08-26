package ru.practicum.shareit.booking.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Метод возвращает коллекцию бронирований по переданному идентификатору инициатора бронирования
     *
     * @param bookerId идентификатор бронирования
     * @param pageable ограничение выборки и порядок сортировки
     * @return коллекция {@link Booking}
     */
    Page<Booking> findAllByBookerEntityId(Long bookerId, Pageable pageable);

    /**
     * Метод возвращает коллекцию текущих бронирований по переданному идентификатору инициатора бронирования
     *
     * @param bookerId идентификатор инициатора бронирования
     * @param date дата поиска
     * @param status статус бронирования
     * @param pageable ограничение выборки и порядок сортировки
     * @return коллекция {@link Booking}
     */
    @Query("SELECT b "
            + "FROM Booking AS b "
            + "WHERE b.booker.entityId = :booker_id "
            + "AND b.startDate < :date "
            + "AND b.endDate > :date "
            + "AND b.status = :status")
    Page<Booking> findAllCurrentBookerBookings(@Param("booker_id") Long bookerId,
                                               @Param("date") LocalDateTime date,
                                               @Param("status") BookingStatus status,
                                               Pageable pageable);

    /**
     * Метод возвращает коллекцию предстоящих бронирований по идентификатору инициатора бронирования
     *
     * @param bookerId идентификатор инициатора бронирования
     * @param date дата поиска
     * @param status статус бронирования
     * @param pageable ограничение выборки и порядок сортировки
     * @return коллекция {@link Booking}
     */
    @Query("SELECT b "
            + "FROM Booking AS b "
            + "WHERE b.booker.entityId = :booker_id "
            + "AND b.startDate > :date "
            + "AND b.endDate > :date "
            + "AND b.status = :status")
    Page<Booking> findAllFutureBookerBookings(@Param("booker_id") Long bookerId,
                                              @Param("date") LocalDateTime date,
                                              @Param("status") BookingStatus status,
                                              Pageable pageable);

    /**
     * Метод возвращает коллекцию прошедших бронирований по идентификатору инициатора бронирования
     *
     * @param bookerId идентификатор инициатора бронирования
     * @param date дата поиска
     * @param status статус бронирования
     * @param pageable ограничение выборки и порядок сортировки
     * @return коллекция {@link Booking}
     */
    @Query("SELECT b "
            + "FROM Booking AS b "
            + "WHERE b.booker.entityId = :booker_id "
            + "AND b.startDate < :date "
            + "AND b.endDate < :date "
            + "AND b.status = :status")
    Page<Booking> findAllPastBookerBookings(@Param("booker_id") Long bookerId,
                                            @Param("date") LocalDateTime date,
                                            @Param("status") BookingStatus status,
                                            Pageable pageable);

    /**
     * Метод возвращает все бронирования по переданному статусу и идентификатору инициатора бронирования
     *
     * @param bookerId идентификатор инициатора бронирования
     * @param status статус бронирования
     * @param pageable ограничение выборки и порядок сортировки
     * @return коллекция {@link Booking}
     */
    @Query("SELECT b "
            + "FROM Booking AS b "
            + "WHERE b.booker.entityId = :booker_id "
            + "AND b.status = :status")
    Page<Booking> findAllBookerBookingsByStatus(@Param("booker_id") Long bookerId,
                                                @Param("status") BookingStatus status,
                                                Pageable pageable);

    /**
     * Метод возвращает все бронирования по переданному идентификатору владельца бронируемых вещей
     *
     * @param entityId идентификатор владельца бронируемых вещей
     * @param pageable ограничение выборки и порядок сортировки
     * @return коллекция {@link Booking}
     */
    Page<Booking> findAllByItemSharerEntityId(Long entityId, Pageable pageable);

    /**
     * Метод возвращает текущие бронирования по переданному идентификатору владельца бронируемых вещей
     *
     * @param ownerId идентификатор владельца бронируемых вещей
     * @param date дата поиска
     * @param status статус бронирования
     * @param pageable ограничение выборки и порядок сортировки
     * @return коллекция {@link Booking}
     */
    @Query("SELECT b "
            + "FROM Booking AS b "
            + "WHERE b.item.sharer.entityId = :owner_id "
            + "AND b.startDate < :date "
            + "AND b.endDate > :date "
            + "AND b.status = :status")
    Page<Booking> findAllCurrentOwnerBookings(@Param("owner_id") Long ownerId,
                                              @Param("date") LocalDateTime date,
                                              @Param("status") BookingStatus status,
                                              Pageable pageable);

    /**
     * Метод возвращает будущие бронирования по переданному идентификатору владельца бронируемых вещей
     *
     * @param ownerId идентификатор владельца бронируемых вещей
     * @param date дата поиска
     * @param status статус бронирования
     * @param pageable ограничение выборки и порядок сортировки
     * @return коллекция {@link Booking}
     */
    @Query("SELECT b "
            + "FROM Booking AS b "
            + "WHERE b.item.sharer.entityId = :owner_id "
            + "AND b.startDate > :date "
            + "AND b.endDate > :date "
            + "AND b.status = :status")
    Page<Booking> findAllFutureOwnerBookings(@Param("owner_id") Long ownerId,
                                             @Param("date") LocalDateTime date,
                                             @Param("status") BookingStatus status,
                                             Pageable pageable);

    /**
     * Метод возвращает прошедшие бронирования по переданному идентификатору владельца бронируемых вещей
     *
     * @param ownerId идентификатор владельца бронируемых вещей
     * @param date дата поиска
     * @param status статус бронирования
     * @param pageable ограничение выборки и порядок сортировки
     * @return коллекция {@link Booking}
     */
    @Query("SELECT b "
            + "FROM Booking AS b "
            + "WHERE b.item.sharer.entityId = :owner_id "
            + "AND b.startDate < :date "
            + "AND b.endDate < :date "
            + "AND b.status = :status")
    Page<Booking> findAllPastOwnerBookings(@Param("owner_id") Long ownerId,
                                           @Param("date") LocalDateTime date,
                                           @Param("status") BookingStatus status,
                                           Pageable pageable);

    /**
     * Метод возвращает все бронирования по переданному статусу бронирования и идентификатору владельца бронируемых
     * вещей
     *
     * @param ownerId идентификатор владельца бронируемых вещей
     * @param status статус бронирования
     * @param pageable ограничение выборки и порядок сортировки
     * @return коллекция {@link Booking}
     */
    @Query("SELECT b "
            + "FROM Booking AS b "
            + "WHERE b.item.sharer.entityId = :owner_id "
            + "AND b.status = :status")
    Page<Booking> findAllOwnerBookingsByStatus(@Param("owner_id") Long ownerId,
                                               @Param("status") BookingStatus status,
                                               Pageable pageable);

    /**
     * Метод возвращает следующее бронирование вещи
     *
     * @param itemId идентификатор вещи
     * @param date дата поиска
     * @param status статус бронирования
     * @param sort порядок сортировки
     * @return экземпляр класса {@link Booking}
     */
    Optional<Booking> findFirstBookingByItemEntityIdAndEndDateIsAfterAndStatus(Long itemId,
                                                                               LocalDateTime date,
                                                                               BookingStatus status,
                                                                               Sort sort);

    /**
     * Метод возвращает последнее завершенное бронирование вещи
     *
     * @param itemId идентификатор вещи
     * @param date дата поиска
     * @param status статус бронирования
     * @param sort порядок сортировки
     * @return экземпляр класса {@link Booking}
     */
    Optional<Booking> findFirstBookingByItemEntityIdAndEndDateIsBeforeAndStatus(Long itemId,
                                                                                LocalDateTime date,
                                                                                BookingStatus status,
                                                                                Sort sort);

    /**
     * Метод проверяет наличие связи между бронированием и владельцем бронируемой вещи
     *
     * @param bookingId идентификатор бронирования
     * @param ownerId идентификатор владельца бронируемой вещи
     * @return результат проверки
     */
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN TRUE ELSE FALSE END "
            + "FROM Booking AS b "
            + "WHERE b.entityId = :booking_id "
            + "AND b.item.sharer.entityId = :owner_id")
    boolean existsByBookingAndOwner(@Param("booking_id") Long bookingId,
                                    @Param("owner_id") Long ownerId);

    /**
     * Метод проверяет наличие связи между пользователем и вещью через завершенное бронирования
     *
     * @param itemId идентификатор вещи
     * @param userId идентификатор пользователя
     * @param date дата поиска
     * @param status статус бронирования
     * @return результат проверки
     */
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN TRUE ELSE FALSE END "
            + "FROM Booking b "
            + "WHERE b.item.entityId = :item_id "
            + "AND b.booker.entityId = :user_id "
            + "AND b.endDate <= :search_date "
            + "AND b.status = :search_status")
    boolean existsByItemAndBooker(@Param("item_id") Long itemId,
                                  @Param("user_id") Long userId,
                                  @Param("search_date") LocalDateTime date,
                                  @Param("search_status") BookingStatus status);
}
