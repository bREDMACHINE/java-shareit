package ru.practicum.shareit.item.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceCreate;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceCreateImplTest {

    private ItemServiceCreate itemServiceCreate;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @BeforeEach
    void setUp() {
        itemServiceCreate = new ItemServiceCreateImpl(itemRepository, userRepository, bookingRepository, commentRepository, itemRequestRepository);
    }

    private final User user1 = new User(1L, "nameUser1", "email@User1");
    private final User user2 = new User(2L, "nameUser2", "email@User2");
    private final ItemRequest itemRequest1 = new ItemRequest(1L,
            "DescriptionItemRequest1", user2,
            LocalDateTime.of(2022, 9, 18, 18, 15,15));
    private final Item item1 = new Item(1L, "nameItem1", "descriptionItem1", true, user1, itemRequest1);
    private final Item item2 = new Item(2L, "nameItem2", "descriptionItem2", true, user2, null);
    private final ItemDto itemDto1 = new ItemDto(1L, "nameItem1", "descriptionItem1", true, 1L);
    private final ItemDto itemDto2 = new ItemDto(2L, "nameItem2", "descriptionItem2", true, null);
    private final ItemDto itemDto3 = new ItemDto(3L, "nameItem3", "descriptionItem3", true, 100L);
    private final Comment comment1 = new Comment(1L, "comment1", item1, user2, LocalDateTime.of(2022, 9, 19, 18, 15,15));
    private final CommentDto commentDto1 = new CommentDto(1L, "comment1", "nameUser2", LocalDateTime.of(2022, 9, 19, 18, 15,15));
    private final Booking booking1 = new Booking(1L, LocalDateTime.of(2022, 9, 18, 19, 15,15),
            LocalDateTime.of(2022, 9, 18, 20, 15,15), item1, user2, Status.APPROVED);

    @Test
    void addItem_ok() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest1));
        when(itemRepository.save(item1)).thenReturn(item1);
        ItemDto itemDtoCheck = itemServiceCreate.addItem(1L, itemDto1);
        assertThat(itemDtoCheck.getId(), equalTo(itemDto1.getId()));
        assertThat(itemDtoCheck.getName(), equalTo(itemDto1.getName()));
        assertThat(itemDtoCheck.getDescription(), equalTo(itemDto1.getDescription()));
        assertThat(itemDtoCheck.getAvailable(), equalTo(itemDto1.getAvailable()));
        assertThat(itemDtoCheck.getRequestId(), equalTo(itemDto1.getRequestId()));
        verify(userRepository).findById(1L);
        verify(itemRequestRepository).findById(1L);
        verify(itemRepository).save(item1);
    }

    @Test
    void addItemWithoutRequesterId_ok() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(itemRepository.save(item2)).thenReturn(item2);
        ItemDto itemDtoCheck = itemServiceCreate.addItem(2L, itemDto2);
        assertThat(itemDtoCheck.getId(), equalTo(itemDto2.getId()));
        assertThat(itemDtoCheck.getName(), equalTo(itemDto2.getName()));
        assertThat(itemDtoCheck.getDescription(), equalTo(itemDto2.getDescription()));
        assertThat(itemDtoCheck.getAvailable(), equalTo(itemDto2.getAvailable()));
        assertThat(itemDtoCheck.getRequestId(), equalTo(itemDto2.getRequestId()));
        verify(userRepository).findById(2L);
        verify(itemRepository).save(item2);
    }

    @Test
    void addItemNotFoundRequesterId_error() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(itemRequestRepository.findById(100L)).thenReturn(Optional.empty());
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemServiceCreate.addItem(2L, itemDto3));
        Assertions.assertEquals("Указанный requestId не существует", exception.getMessage());
        verify(userRepository).findById(2L);
        verify(itemRequestRepository).findById(100L);
    }

    @Test
    void addItemNotFoundUserId_error() {
        when(userRepository.findById(100L)).thenReturn(Optional.empty());
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemServiceCreate.addItem(100L, itemDto3));
        Assertions.assertEquals("Указанный userId не существует", exception.getMessage());
        verify(userRepository).findById(100L);
    }

    @Test
    void addComment_ok() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(bookingRepository.findFirstByBookerIdAndItemIdAndEndBefore(eq(2L), eq(1L), ArgumentMatchers.any()))
                .thenReturn(Optional.of(booking1));
        when(commentRepository.save(comment1)).thenReturn(comment1);
        CommentDto commentDtoCheck = itemServiceCreate.addComment(2L, 1L, commentDto1);
        assertThat(commentDtoCheck.getId(), equalTo(commentDto1.getId()));
        assertThat(commentDtoCheck.getAuthorName(), equalTo(commentDto1.getAuthorName()));
        assertThat(commentDtoCheck.getText(), equalTo(commentDto1.getText()));
        verify(userRepository).findById(2L);
        verify(itemRepository).findById(1L);
        verify(bookingRepository).findFirstByBookerIdAndItemIdAndEndBefore(eq(2L), eq(1L), ArgumentMatchers.any());
        verify(commentRepository).save(comment1);
    }

    @Test
    void addCommentBadRequest_error() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(bookingRepository.findFirstByBookerIdAndItemIdAndEndBefore(eq(2L), eq(1L), ArgumentMatchers.any()))
                .thenReturn(Optional.empty());
        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemServiceCreate.addComment(2L, 1L, commentDto1));
        Assertions.assertEquals("Добавление комментариев недоступно", exception.getMessage());
        verify(userRepository).findById(2L);
        verify(itemRepository).findById(1L);
        verify(bookingRepository).findFirstByBookerIdAndItemIdAndEndBefore(eq(2L), eq(1L), ArgumentMatchers.any());
    }
}