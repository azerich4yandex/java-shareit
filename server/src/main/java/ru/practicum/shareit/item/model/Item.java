package ru.practicum.shareit.item.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * Вещь.
 */
@Table(name = "items")
@Entity
@Builder
@EqualsAndHashCode(of = "entityId")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    /**
     * Идентификатор сущности
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long entityId;

    /**
     * Идентификатор владельца
     */
    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private User sharer;

    /**
     * Краткое наименование
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Краткое описание
     */
    @Column(name = "description", nullable = false)
    private String description;

    /**
     * Признак доступности вещи
     */
    @Column(name = "is_available")
    private Boolean available;

    /**
     * Идентификатор запроса
     */
    @ManyToOne
    @JoinColumn(name = "request_id", referencedColumnName = "id")
    private ItemRequest request;
}
