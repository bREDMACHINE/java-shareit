package ru.practicum.shareit.item.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceRead;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceReedImplTest {

    private ItemServiceRead itemServiceRead;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        itemServiceRead = new ItemServiceReedImpl(itemRepository, userRepository, bookingRepository, commentRepository);
    }

    private final User user1 = new User(1L, "nameUser1", "email@User1");
    private final User user2 = new User(2L, "nameUser2", "email@User2");
    private final ItemRequest itemRequest1 = new ItemRequest(1L,
            "DescriptionItemRequest1", user2,
            LocalDateTime.of(2022, 9, 18, 18, 15,15));
    private final Item item1 = new Item(1L, "nameItem1", "descriptionItem1", true, user1, itemRequest1);
    private final Item item3 = new Item(3L, "nameItem3", " ", true, user2, null);
    private final Item item4 = new Item(4L, " ", "descriptionItem4", true, user2, null);
    private final Item item5 = new Item(5L, "nameItem5", "descriptionItem4", false, user2, null);
    private final ItemDto itemDto1 = new ItemDto(1L, "nameItem1", "descriptionItem1", true, 1L);
    private final Comment comment1 = new Comment(1L, "comment1", item1, user2, LocalDateTime.of(2022, 9, 19, 18, 15,15));
    private final Comment comment2 = new Comment(2L, "comment2", item1, user2, LocalDateTime.of(2022, 10, 18, 18, 15,15));
    private final CommentDto commentDto1 = new CommentDto(1L, "comment1", "nameUser2", LocalDateTime.of(2022, 9, 19, 18, 15,15));
    private final CommentDto commentDto2 = new CommentDto(2L, "comment2", "nameUser2", LocalDateTime.of(2022, 10, 18, 18, 15,15));
    private final Booking booking1 = new Booking(1L, LocalDateTime.of(2022, 9, 18, 19, 15,15),
            LocalDateTime.of(2022, 9, 18, 20, 15,15), item1, user2, Status.APPROVED);
    private final Booking booking2 = new Booking(2L, LocalDateTime.of(2022, 9, 29, 19, 15,15),
            LocalDateTime.of(2022, 9, 29, 20, 15,15), item1, user2, Status.APPROVED);
    private final ItemOutDto itemOutDto = new ItemOutDto(1L, "nameItem1", "descriptionItem1", true,
            new ItemOutDto.BookingForItemOutDto(1L, 2L), new ItemOutDto.BookingForItemOutDto(2L, 2L),
            List.of(commentDto1, commentDto2));

    @Test
    void getItemOwner_ok() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(commentRepository.findAllByItemId(1L)).thenReturn(List.of(comment1, comment2));
        when(bookingRepository.findFirstByItemIdAndStartBeforeAndStatus(eq(1L), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(booking1);
        when(bookingRepository.findFirstByItemIdAndStartAfterAndStatus(eq(1L), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(booking2);
        ItemOutDto itemOutDtoCheck = itemServiceRead.getItem(1L, 1L);
        assertThat(itemOutDtoCheck.getId(), equalTo(itemOutDto.getId()));
        assertThat(itemOutDtoCheck.getName(), equalTo(itemOutDto.getName()));
        assertThat(itemOutDtoCheck.getDescription(), equalTo(itemOutDto.getDescription()));
        assertThat(itemOutDtoCheck.getAvailable(), equalTo(itemOutDto.getAvailable()));
        assertThat(itemOutDtoCheck.getComments(), equalTo(itemOutDto.getComments()));
        assertThat(itemOutDtoCheck.getNextBooking(), equalTo(itemOutDto.getNextBooking()));
        assertThat(itemOutDtoCheck.getLastBooking(), equalTo(itemOutDto.getLastBooking()));
        assertThat(itemOutDtoCheck.getComments(),equalTo(itemOutDto.getComments()));
        verify(userRepository).findById(1L);
        verify(itemRepository).findById(1L);
        verify(commentRepository).findAllByItemId(1L);
        verify(bookingRepository).findFirstByItemIdAndStartBeforeAndStatus(eq(1L), ArgumentMatchers.any(), ArgumentMatchers.any());
        verify(bookingRepository).findFirstByItemIdAndStartAfterAndStatus(eq(1L), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void getItemOther_ok() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(commentRepository.findAllByItemId(1L)).thenReturn(List.of(comment1, comment2));
        ItemOutDto itemOutDtoCheck = itemServiceRead.getItem(2L, 1L);
        assertThat(itemOutDtoCheck.getId(), equalTo(itemOutDto.getId()));
        assertThat(itemOutDtoCheck.getName(), equalTo(itemOutDto.getName()));
        assertThat(itemOutDtoCheck.getDescription(), equalTo(itemOutDto.getDescription()));
        assertThat(itemOutDtoCheck.getAvailable(), equalTo(itemOutDto.getAvailable()));
        assertThat(itemOutDtoCheck.getComments(), equalTo(itemOutDto.getComments()));
        assertThat(itemOutDtoCheck.getNextBooking(), equalTo(null));
        assertThat(itemOutDtoCheck.getLastBooking(), equalTo(null));
        assertThat(itemOutDtoCheck.getComments(),equalTo(itemOutDto.getComments()));
        verify(userRepository).findById(2L);
        verify(itemRepository).findById(1L);
        verify(commentRepository).findAllByItemId(1L);
    }

    @Test
    void findAllItems_ok() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findItemsByOwnerIdOrderByIdAsc(1L)).thenReturn(List.of(item1));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(commentRepository.findAllByItemId(1L)).thenReturn(List.of(comment1, comment2));
        when(bookingRepository.findFirstByItemIdAndStartBeforeAndStatus(eq(1L), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(booking1);
        when(bookingRepository.findFirstByItemIdAndStartAfterAndStatus(eq(1L), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(booking2);
        List<ItemOutDto> itemOutDtoListCheck = itemServiceRead.findAllItems(1L);
        assertThat(itemOutDtoListCheck.get(0).getId(), equalTo(itemOutDto.getId()));
        assertThat(itemOutDtoListCheck.get(0).getName(), equalTo(itemOutDto.getName()));
        assertThat(itemOutDtoListCheck.get(0).getDescription(), equalTo(itemOutDto.getDescription()));
        assertThat(itemOutDtoListCheck.get(0).getAvailable(), equalTo(itemOutDto.getAvailable()));
        assertThat(itemOutDtoListCheck.get(0).getComments(), equalTo(itemOutDto.getComments()));
        assertThat(itemOutDtoListCheck.get(0).getNextBooking(), equalTo(itemOutDto.getNextBooking()));
        assertThat(itemOutDtoListCheck.get(0).getLastBooking(), equalTo(itemOutDto.getLastBooking()));
        assertThat(itemOutDtoListCheck.get(0).getComments(),equalTo(itemOutDto.getComments()));
        verify(userRepository, times(2)).findById(1L);
        verify(itemRepository).findById(1L);
        verify(commentRepository).findAllByItemId(1L);
        verify(bookingRepository).findFirstByItemIdAndStartBeforeAndStatus(eq(1L), ArgumentMatchers.any(), ArgumentMatchers.any());
        verify(bookingRepository).findFirstByItemIdAndStartAfterAndStatus(eq(1L), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void searchItems_ok() {
        when(itemRepository.search("text")).thenReturn(List.of(item1));
        List<ItemDto> itemDtoListCheck = itemServiceRead.searchItems(1L, "text");
        assertThat(itemDtoListCheck.get(0).getId(), equalTo(itemDto1.getId()));
        assertThat(itemDtoListCheck.get(0).getName(), equalTo(itemDto1.getName()));
        assertThat(itemDtoListCheck.get(0).getDescription(), equalTo(itemDto1.getDescription()));
        assertThat(itemDtoListCheck.get(0).getAvailable(), equalTo(itemDto1.getAvailable()));
        assertThat(itemDtoListCheck.get(0).getRequestId(), equalTo(itemDto1.getRequestId()));
        verify(itemRepository).search("text");
    }

    @Test
    void searchItemsIsBlankDescription() {
        when(itemRepository.search(" ")).thenReturn(List.of(item3));
        List<ItemDto> itemDtoListCheck = itemServiceRead.searchItems(1L, " ");
        assertThat(itemDtoListCheck.size(), equalTo(0));
        verify(itemRepository).search(" ");
    }

    @Test
    void searchItemsIsBlankName() {
        when(itemRepository.search(" ")).thenReturn(List.of(item4));
        List<ItemDto> itemDtoListCheck = itemServiceRead.searchItems(1L, " ");
        assertThat(itemDtoListCheck.size(), equalTo(0));
        verify(itemRepository).search(" ");
    }

    @Test
    void searchItemsNotAvailable() {
        when(itemRepository.search(" ")).thenReturn(List.of(item5));
        List<ItemDto> itemDtoListCheck = itemServiceRead.searchItems(1L, " ");
        assertThat(itemDtoListCheck.size(), equalTo(0));
        verify(itemRepository).search(" ");
    }
}