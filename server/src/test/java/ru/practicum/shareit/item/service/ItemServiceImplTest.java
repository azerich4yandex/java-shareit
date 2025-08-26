package ru.practicum.shareit.item.service;

import jakarta.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.commons.exceptions.NotFoundException;
import ru.practicum.shareit.commons.exceptions.UserIsNotSharerException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Обработка данных на уровне сервиса ItemService")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {

    @Autowired
    private final ItemServiceImpl itemService;

    @MockBean
    private final ItemRepository itemRepository;

    @MockBean
    private final UserRepository userRepository;

    @MockBean
    private final ItemRequestRepository itemRequestRepository;

    @MockBean
    private final BookingRepository bookingRepository;

    @MockBean
    private final CommentRepository commentRepository;

    private User owner;
    private User booker;
    private ItemRequest itemRequest;
    private Item item;
    private Booking lastBooking;
    private Booking nextBooking;
    private Comment comment;
    private ItemCreateDto itemCreateDto;
    private CommentCreateDto commentCreateDto;
    private ItemUpdateDto itemUpdateDto;

    private static Page<Item> getPageFromList(List<Item> list) {
        return new PageImpl<>(list, PageRequest.of(0, list.isEmpty() ? 1 : list.size()), list.size());
    }

    @BeforeEach
    void setUp() {
        Random random = new Random();

        owner = User.builder()
                .entityId(Math.abs(random.nextLong()))
                .name("Owner Test")
                .email("owner@system.com")
                .build();

        booker = User.builder()
                .entityId(Math.abs(random.nextLong()))
                .name("Booker Test")
                .email("booker@system.com")
                .build();

        itemRequest = ItemRequest.builder()
                .entityId(Math.abs(random.nextLong()))
                .description("Request description")
                .requestor(booker)
                .created(LocalDateTime.now().minusDays(3))
                .build();

        item = Item.builder()
                .entityId(Math.abs(random.nextLong()))
                .sharer(owner)
                .name("Item test")
                .description("Item description")
                .available(true)
                .request(itemRequest)
                .build();

        lastBooking = Booking.builder()
                .entityId(Math.abs(random.nextLong()))
                .startDate(LocalDateTime.now().minusDays(3))
                .endDate(LocalDateTime.now().minusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        nextBooking = Booking.builder()
                .entityId(Math.abs(random.nextLong()))
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        comment = Comment.builder()
                .entityId(Math.abs(random.nextLong()))
                .text("Comment text")
                .item(item)
                .author(booker)
                .created(lastBooking.getEndDate().plusDays(1))
                .build();

        itemCreateDto = ItemCreateDto.builder()
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest().getEntityId())
                .build();

        commentCreateDto = CommentCreateDto.builder()
                .text(comment.getText())
                .authorId(comment.getAuthor().getEntityId())
                .created(comment.getCreated())
                .build();

        itemUpdateDto = ItemUpdateDto.builder()
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    @AfterEach
    void tearDown() {
        owner = null;
        booker = null;
        itemRequest = null;
        item = null;
        lastBooking = null;
        nextBooking = null;
        comment = null;
        itemCreateDto = null;
        commentCreateDto = null;
    }

    @DisplayName("Получение списка вещей по идентификатору владельца")
    @Test
    void findAllByOwner() {
        when(itemRepository.findAllBySharerEntityId(anyLong(), any()))
                .thenReturn(getPageFromList(List.of(item)));
        when(itemRepository.findByRequestEntityIdIn(any(), any()))
                .thenReturn(List.of(item));
        when(bookingRepository.findFirstBookingByItemEntityIdAndEndDateIsBeforeAndStatus(anyLong(), any(), any(),
                any()))
                .thenReturn(Optional.ofNullable(lastBooking));
        when(bookingRepository.findFirstBookingByItemEntityIdAndEndDateIsAfterAndStatus(anyLong(), any(), any(), any()))
                .thenReturn(Optional.ofNullable(nextBooking));
        when(commentRepository.findAllByItemEntityId(anyLong(), any()))
                .thenReturn(List.of(comment));

        Collection<ItemFullDto> itemList = itemService.findAllByOwner(owner.getEntityId(), 0, 10);
        assertNotNull(itemList);
        assertFalse(itemList.isEmpty());
        assertEquals(1, itemList.size());

        Optional<ItemFullDto> itemDtoOpt = itemList.stream().findFirst();
        assertTrue(itemDtoOpt.isPresent());

        ItemFullDto itemDto = itemDtoOpt.get();
        assertNotNull(itemDto);
        assertNotNull(itemDto.getId());
        assertEquals(item.getEntityId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertEquals(owner.getEntityId(), itemDto.getSharer().getId());
        assertEquals(owner.getName(), itemDto.getSharer().getName());
        assertEquals(owner.getEmail(), itemDto.getSharer().getEmail());
        assertEquals(lastBooking.getEntityId(), itemDto.getLastBooking().getId());
        assertEquals(lastBooking.getStartDate(), itemDto.getLastBooking().getStart());
        assertEquals(lastBooking.getEndDate(), itemDto.getLastBooking().getEnd());
        assertEquals(lastBooking.getBooker().getEntityId(), itemDto.getLastBooking().getBookerId());
        assertEquals(nextBooking.getEntityId(), itemDto.getNextBooking().getId());
        assertEquals(nextBooking.getStartDate(), itemDto.getNextBooking().getStart());
        assertEquals(nextBooking.getEndDate(), itemDto.getNextBooking().getEnd());
        assertEquals(nextBooking.getBooker().getEntityId(), itemDto.getNextBooking().getBookerId());
        assertEquals(itemRequest.getEntityId(), itemDto.getRequest().getId());
        assertEquals(itemRequest.getDescription(), itemDto.getRequest().getDescription());
        assertEquals(itemRequest.getCreated(), itemDto.getRequest().getCreated());
        assertEquals(booker.getEntityId(), itemDto.getRequest().getRequestor().getId());
        assertEquals(booker.getName(), itemDto.getRequest().getRequestor().getName());
        assertEquals(booker.getEmail(), itemDto.getRequest().getRequestor().getEmail());
        assertNotNull(itemDto.getRequest().getItems());
        assertFalse(itemDto.getRequest().getItems().isEmpty());

        Optional<ItemShortDto> itemShortDtoOpt = itemDto.getRequest().getItems().stream().findFirst();
        assertNotNull(itemShortDtoOpt);
        ItemShortDto itemShortDto = itemShortDtoOpt.get();
        assertNotNull(itemShortDto);
        assertEquals(item.getEntityId(), itemShortDto.getId());
        assertEquals(item.getName(), itemShortDto.getName());
        assertEquals(item.getDescription(), itemShortDto.getDescription());
        assertEquals(item.getAvailable(), itemShortDto.getAvailable());
        assertEquals(owner.getEntityId(), itemShortDto.getSharer().getId());
        assertEquals(owner.getName(), itemShortDto.getSharer().getName());
        assertEquals(owner.getEmail(), itemShortDto.getSharer().getEmail());
        assertEquals(itemRequest.getEntityId(), itemShortDto.getRequest().getId());
        assertEquals(itemRequest.getDescription(), itemShortDto.getRequest().getDescription());
        assertEquals(itemRequest.getCreated(), itemShortDto.getRequest().getCreated());
        assertEquals(booker.getEntityId(), itemShortDto.getRequest().getRequestor().getId());
        assertEquals(booker.getName(), itemShortDto.getRequest().getRequestor().getName());
        assertEquals(booker.getEmail(), itemShortDto.getRequest().getRequestor().getEmail());

        assertNotNull(itemDto.getComments());
        assertFalse(itemDto.getComments().isEmpty());
        assertEquals(1, itemDto.getComments().size());
        Optional<CommentShortDto> commentShortDtoOpt = itemDto.getComments().stream().findFirst();
        assertNotNull(commentShortDtoOpt);
        assertTrue(commentShortDtoOpt.isPresent());
        CommentShortDto commentShortDto = commentShortDtoOpt.get();
        assertNotNull(commentShortDto);
        assertNotNull(commentShortDto.getId());
        assertEquals(comment.getEntityId(), commentShortDto.getId());
        assertEquals(comment.getText(), commentShortDto.getText());
        assertEquals(comment.getAuthor().getName(), commentShortDto.getAuthorName());
        assertEquals(comment.getCreated(), commentShortDto.getCreated());
    }


    @DisplayName("Получение пустого списка вещей по идентификатору владельца")
    @Test
    void findAllByOwnerEmptyList() {
        when(itemRepository.findAllBySharerEntityId(anyLong(), any()))
                .thenReturn(getPageFromList(new ArrayList<>()));

        Collection<ItemFullDto> itemList = itemService.findAllByOwner(owner.getEntityId(), 0, 10);
        assertNotNull(itemList);
        assertTrue(itemList.isEmpty());
    }

    @DisplayName("Получение списка вещей по вхождению подстроки")
    @Test
    void findByText() {
        when(itemRepository.findAllByNameAndAvailable(anyString(), anyBoolean(), any()))
                .thenReturn(getPageFromList(List.of(item)));

        Collection<ItemShortDto> itemList = itemService.findByText(item.getName(), 0, 10);
        assertNotNull(itemList);
        assertFalse(itemList.isEmpty());
        assertEquals(1, itemList.size());

        Optional<ItemShortDto> itemShortDtoOpt = itemList.stream().findFirst();
        assertNotNull(itemShortDtoOpt);
        assertTrue(itemShortDtoOpt.isPresent());
        ItemShortDto itemShortDto = itemShortDtoOpt.get();
        assertNotNull(itemShortDto);
        assertEquals(item.getEntityId(), itemShortDto.getId());
        assertEquals(item.getName(), itemShortDto.getName());
        assertEquals(item.getDescription(), itemShortDto.getDescription());
        assertEquals(item.getAvailable(), itemShortDto.getAvailable());
        assertEquals(owner.getEntityId(), itemShortDto.getSharer().getId());
        assertEquals(owner.getName(), itemShortDto.getSharer().getName());
        assertEquals(owner.getEmail(), itemShortDto.getSharer().getEmail());
        assertEquals(itemRequest.getEntityId(), itemShortDto.getRequest().getId());
        assertEquals(booker.getEntityId(), itemShortDto.getRequest().getRequestor().getId());
        assertEquals(booker.getName(), itemShortDto.getRequest().getRequestor().getName());
        assertEquals(booker.getEmail(), itemShortDto.getRequest().getRequestor().getEmail());
        assertEquals(itemRequest.getDescription(), itemShortDto.getRequest().getDescription());
        assertEquals(itemRequest.getCreated(), itemShortDto.getRequest().getCreated());
    }

    @DisplayName("Получение пустого списка вещей по вхождению подстроки")
    @Test
    void findByTextEmptyList() {
        when(itemRepository.findAllByNameAndAvailable(anyString(), anyBoolean(), any()))
                .thenReturn(getPageFromList(new ArrayList<>()));

        Collection<ItemShortDto> itemList = itemService.findByText(item.getName(), 0, 10);
        assertNotNull(itemList);
        assertTrue(itemList.isEmpty());
    }

    @DisplayName("Получение вещи по идентификатору")
    @Test
    void findById() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(itemRepository.findByRequestEntityIdIn(any(), any()))
                .thenReturn(List.of(item));
        when(bookingRepository.findFirstBookingByItemEntityIdAndEndDateIsBeforeAndStatus(anyLong(), any(), any(),
                any()))
                .thenReturn(Optional.ofNullable(lastBooking));
        when(bookingRepository.findFirstBookingByItemEntityIdAndEndDateIsAfterAndStatus(anyLong(), any(), any(), any()))
                .thenReturn(Optional.ofNullable(nextBooking));
        when(commentRepository.findAllByItemEntityId(anyLong(), any()))
                .thenReturn(List.of(comment));

        ItemFullDto itemDto = itemService.findById(owner.getEntityId(), item.getEntityId());
        assertNotNull(itemDto);
        assertNotNull(itemDto.getId());
        assertEquals(item.getEntityId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertEquals(owner.getEntityId(), itemDto.getSharer().getId());
        assertEquals(owner.getName(), itemDto.getSharer().getName());
        assertEquals(owner.getEmail(), itemDto.getSharer().getEmail());
        assertEquals(lastBooking.getEntityId(), itemDto.getLastBooking().getId());
        assertEquals(lastBooking.getStartDate(), itemDto.getLastBooking().getStart());
        assertEquals(lastBooking.getEndDate(), itemDto.getLastBooking().getEnd());
        assertEquals(lastBooking.getBooker().getEntityId(), itemDto.getLastBooking().getBookerId());
        assertEquals(nextBooking.getEntityId(), itemDto.getNextBooking().getId());
        assertEquals(nextBooking.getStartDate(), itemDto.getNextBooking().getStart());
        assertEquals(nextBooking.getEndDate(), itemDto.getNextBooking().getEnd());
        assertEquals(nextBooking.getBooker().getEntityId(), itemDto.getNextBooking().getBookerId());
        assertEquals(itemRequest.getEntityId(), itemDto.getRequest().getId());
        assertEquals(itemRequest.getDescription(), itemDto.getRequest().getDescription());
        assertEquals(itemRequest.getCreated(), itemDto.getRequest().getCreated());
        assertEquals(booker.getEntityId(), itemDto.getRequest().getRequestor().getId());
        assertEquals(booker.getName(), itemDto.getRequest().getRequestor().getName());
        assertEquals(booker.getEmail(), itemDto.getRequest().getRequestor().getEmail());
        assertNotNull(itemDto.getRequest().getItems());
        assertFalse(itemDto.getRequest().getItems().isEmpty());

        Optional<ItemShortDto> itemShortDtoOpt = itemDto.getRequest().getItems().stream().findFirst();
        assertNotNull(itemShortDtoOpt);
        ItemShortDto itemShortDto = itemShortDtoOpt.get();
        assertNotNull(itemShortDto);
        assertEquals(item.getEntityId(), itemShortDto.getId());
        assertEquals(item.getName(), itemShortDto.getName());
        assertEquals(item.getDescription(), itemShortDto.getDescription());
        assertEquals(item.getAvailable(), itemShortDto.getAvailable());
        assertEquals(owner.getEntityId(), itemShortDto.getSharer().getId());
        assertEquals(owner.getName(), itemShortDto.getSharer().getName());
        assertEquals(owner.getEmail(), itemShortDto.getSharer().getEmail());
        assertEquals(itemRequest.getEntityId(), itemShortDto.getRequest().getId());
        assertEquals(itemRequest.getDescription(), itemShortDto.getRequest().getDescription());
        assertEquals(itemRequest.getCreated(), itemShortDto.getRequest().getCreated());
        assertEquals(booker.getEntityId(), itemShortDto.getRequest().getRequestor().getId());
        assertEquals(booker.getName(), itemShortDto.getRequest().getRequestor().getName());
        assertEquals(booker.getEmail(), itemShortDto.getRequest().getRequestor().getEmail());

        assertNotNull(itemDto.getComments());
        assertFalse(itemDto.getComments().isEmpty());
        assertEquals(1, itemDto.getComments().size());
        Optional<CommentShortDto> commentShortDtoOpt = itemDto.getComments().stream().findFirst();
        assertNotNull(commentShortDtoOpt);
        assertTrue(commentShortDtoOpt.isPresent());
        CommentShortDto commentShortDto = commentShortDtoOpt.get();
        assertNotNull(commentShortDto);
        assertNotNull(commentShortDto.getId());
        assertEquals(comment.getEntityId(), commentShortDto.getId());
        assertEquals(comment.getText(), commentShortDto.getText());
        assertEquals(comment.getAuthor().getName(), commentShortDto.getAuthorName());
        assertEquals(comment.getCreated(), commentShortDto.getCreated());
    }

    @DisplayName("Вызов исключения NotFoundException при получении вещи по идентификатору")
    @Test
    void findByIdWith404Exception() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.findById(owner.getEntityId(), item.getEntityId()));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.findById(owner.getEntityId(), item.getEntityId()));
    }

    @DisplayName("Создание вещи")
    @Test
    void create() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(itemRequest));
        when(itemRepository.save(any()))
                .thenReturn(item);

        ItemShortDto itemShortDto = itemService.create(owner.getEntityId(), itemCreateDto);
        assertNotNull(itemShortDto);
        assertNotNull(itemShortDto.getId());
        assertEquals(item.getEntityId(), itemShortDto.getId());
        assertEquals(item.getName(), itemShortDto.getName());
        assertEquals(item.getDescription(), itemShortDto.getDescription());
        assertEquals(item.getAvailable(), itemShortDto.getAvailable());
        assertEquals(owner.getEntityId(), itemShortDto.getSharer().getId());
        assertEquals(owner.getName(), itemShortDto.getSharer().getName());
        assertEquals(owner.getEmail(), itemShortDto.getSharer().getEmail());
        assertEquals(itemRequest.getEntityId(), itemShortDto.getRequest().getId());
        assertEquals(itemRequest.getDescription(), itemShortDto.getRequest().getDescription());
        assertEquals(itemRequest.getCreated(), itemShortDto.getRequest().getCreated());
        assertEquals(booker.getEntityId(), itemShortDto.getRequest().getRequestor().getId());
        assertEquals(booker.getName(), itemShortDto.getRequest().getRequestor().getName());
        assertEquals(booker.getEmail(), itemShortDto.getRequest().getRequestor().getEmail());

    }

    @DisplayName("Вызов исключения NotFoundException при создании вещи")
    @Test
    void createWith404Exception() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.create(owner.getEntityId(), itemCreateDto));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        when(itemRequestRepository.findById(anyLong()))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.create(owner.getEntityId(), itemCreateDto));
    }


    @DisplayName("Вызов исключения RuntimeException при создании вещи")
    @Test
    void createWith500Exception() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        when(itemRepository.save(any()))
                .thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> itemService.create(owner.getEntityId(), itemCreateDto));
    }

    @DisplayName("Создание комментария")
    @Test
    void createComment() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booker));
        when(bookingRepository.existsByItemAndBooker(anyLong(), anyLong(), any(), any()))
                .thenReturn(true);
        when(commentRepository.save(any()))
                .thenReturn(comment);

        CommentShortDto commentShortDto = itemService.createComment(item.getEntityId(), booker.getEntityId(),
                commentCreateDto);
        assertNotNull(commentShortDto);
        assertNotNull(commentShortDto.getId());
        assertEquals(comment.getEntityId(), commentShortDto.getId());
        assertEquals(comment.getText(), commentShortDto.getText());
        assertEquals(comment.getAuthor().getName(), commentShortDto.getAuthorName());
        assertEquals(comment.getCreated(), commentShortDto.getCreated());
    }

    @DisplayName("Вызов исключения ValidationException при создании комментария")
    @Test
    void createCommentWith400Exception() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booker));
        when(bookingRepository.existsByItemAndBooker(anyLong(), anyLong(), any(), any()))
                .thenReturn(false);

        assertThrows(ValidationException.class,
                () -> itemService.createComment(item.getEntityId(), booker.getEntityId(), commentCreateDto));
    }


    @DisplayName("Вызов исключения NotFoundException при создании комментария")
    @Test
    void createCommentWith404Exception() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.createComment(item.getEntityId(), booker.getEntityId(), commentCreateDto));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.createComment(item.getEntityId(), booker.getEntityId(), commentCreateDto));
    }


    @DisplayName("Вызов исключения RuntimeException при создании комментария")
    @Test
    void createCommentWith500Exception() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(itemRepository.save(any()))
                .thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class,
                () -> itemService.createComment(item.getEntityId(), booker.getEntityId(), commentCreateDto));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.createComment(item.getEntityId(), booker.getEntityId(), commentCreateDto));
    }

    @DisplayName("Обновление вещи по идентификатору")
    @Test
    void update() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(itemRepository.save(any()))
                .thenReturn(item);

        ItemShortDto itemShortDto = itemService.update(owner.getEntityId(), item.getEntityId(), itemUpdateDto);
        assertNotNull(itemShortDto);
        assertNotNull(itemShortDto.getId());
        assertEquals(item.getEntityId(), itemShortDto.getId());
        assertEquals(item.getName(), itemShortDto.getName());
        assertEquals(item.getDescription(), itemShortDto.getDescription());
        assertEquals(item.getAvailable(), itemShortDto.getAvailable());
        assertEquals(owner.getEntityId(), itemShortDto.getSharer().getId());
        assertEquals(owner.getName(), itemShortDto.getSharer().getName());
        assertEquals(owner.getEmail(), itemShortDto.getSharer().getEmail());
        assertEquals(itemRequest.getEntityId(), itemShortDto.getRequest().getId());
        assertEquals(itemRequest.getDescription(), itemShortDto.getRequest().getDescription());
        assertEquals(itemRequest.getCreated(), itemShortDto.getRequest().getCreated());
        assertEquals(booker.getEntityId(), itemShortDto.getRequest().getRequestor().getId());
        assertEquals(booker.getName(), itemShortDto.getRequest().getRequestor().getName());
        assertEquals(booker.getEmail(), itemShortDto.getRequest().getRequestor().getEmail());
    }

    @DisplayName("Вызов исключения UserIsNotSharerException при обновлении вещи по идентификатору")
    @Test
    void updateWith403Exception() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        assertThrows(UserIsNotSharerException.class,
                () -> itemService.update(booker.getEntityId(), item.getEntityId(), itemUpdateDto));
    }

    @DisplayName("Вызов исключения NotFoundException при обновлении вещи по идентификатору")
    @Test
    void updateWith404Exception() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.update(owner.getEntityId(), item.getEntityId(), itemUpdateDto));
    }

    @DisplayName("Удаление вещи по идентификатору")
    @Test
    void delete() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        itemService.delete(owner.getEntityId(), item.getEntityId());

        verify(itemRepository).deleteById(item.getEntityId());
    }

    @DisplayName("Вызов исключения ValidationException при удалении вещи по идентификатору")
    @Test
    void deleteWith400Exception() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booker));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        assertThrows(ValidationException.class,
                () -> itemService.delete(booker.getEntityId(), item.getEntityId()));
    }

    @DisplayName("Вызов исключения NotFoundException при удалении вещи по идентификатору")
    @Test
    void deleteWith404Exception() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.delete(owner.getEntityId(), item.getEntityId()));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.delete(owner.getEntityId(), item.getEntityId()));
    }
}