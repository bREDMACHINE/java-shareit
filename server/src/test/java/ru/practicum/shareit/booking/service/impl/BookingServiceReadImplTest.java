package ru.practicum.shareit.booking.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.service.BookingServiceRead;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceReadImplTest {

    private BookingServiceRead bookingServiceRead;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;

    @BeforeEach
    void setUp() {
        bookingServiceRead = new BookingServiceReadImpl(bookingRepository, userRepository);
    }

    private final User user1 = new User(1L, "nameUser1", "email@User1");
    private final User user2 = new User(2L, "nameUser2", "email@User2");
    private final User user3 = new User(3L, "nameUser3", "email@User3");
    private final ItemRequest itemRequest1 = new ItemRequest(1L,
            "DescriptionItemRequest1", user2,
            LocalDateTime.of(2022, 9, 18, 18, 15,15));
    private final Item item1 = new Item(1L, "nameItem1", "descriptionItem1", true, user1, itemRequest1);
    private final Item item2 = new Item(2L, "nameItem2", "descriptionItem2", false, user1, null);
    private final Booking booking1 = new Booking(1L, LocalDateTime.of(2022, 9, 18, 19, 15,15),
            LocalDateTime.of(2022, 9, 18, 20, 15,15), item1, user2, Status.WAITING);
    private final Booking booking2 = new Booking(2L, LocalDateTime.of(2022, 10, 30, 19, 15,15),
            LocalDateTime.of(2022, 10, 30, 20, 15,15), item1, user2, Status.APPROVED);
    private final Booking booking3 = new Booking(2L, LocalDateTime.of(2022, 9, 28, 19, 15,15),
            LocalDateTime.of(2022, 10, 29, 20, 15,15), item2, user2, Status.REJECTED);

    @Test
    void getStatusOwner_ok() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(bookingRepository.findById(2L)).thenReturn(Optional.of(booking2));
        BookingOutDto bookingOutDtoCheck = bookingServiceRead.getStatus(1L, 2L);
        assertThat(bookingOutDtoCheck.getId(), equalTo(booking2.getId()));
        assertThat(bookingOutDtoCheck.getEnd().toString(), equalTo(booking2.getEnd().toString()));
        assertThat(bookingOutDtoCheck.getStart().toString(), equalTo(booking2.getStart().toString()));
        assertThat(bookingOutDtoCheck.getItem().getId(), equalTo(booking2.getItem().getId()));
        assertThat(bookingOutDtoCheck.getStatus(), equalTo(booking2.getStatus()));
        assertThat(bookingOutDtoCheck.getBooker().getId(), equalTo(booking2.getBooker().getId()));
        verify(userRepository).findById(1L);
        verify(bookingRepository).findById(2L);
    }

    @Test
    void getStatusBooker_ok() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(bookingRepository.findById(2L)).thenReturn(Optional.of(booking2));
        BookingOutDto bookingOutDtoCheck = bookingServiceRead.getStatus(2L, 2L);
        assertThat(bookingOutDtoCheck.getId(), equalTo(booking2.getId()));
        assertThat(bookingOutDtoCheck.getEnd().toString(), equalTo(booking2.getEnd().toString()));
        assertThat(bookingOutDtoCheck.getStart().toString(), equalTo(booking2.getStart().toString()));
        assertThat(bookingOutDtoCheck.getItem().getId(), equalTo(booking2.getItem().getId()));
        assertThat(bookingOutDtoCheck.getStatus(), equalTo(booking2.getStatus()));
        assertThat(bookingOutDtoCheck.getBooker().getId(), equalTo(booking2.getBooker().getId()));
        verify(userRepository).findById(2L);
        verify(bookingRepository).findById(2L);
    }

    @Test
    void getStatusNotFoundNotOwner_error() {
        when(userRepository.findById(3L)).thenReturn(Optional.of(user3));
        when(bookingRepository.findById(2L)).thenReturn(Optional.of(booking2));
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingServiceRead.getStatus(3L, 2L));
        Assertions.assertEquals("userId не является владельцем или автором брони", exception.getMessage());
        verify(userRepository).findById(3L);
        verify(bookingRepository).findById(2L);
    }

    @Test
    void getStateBookerAll_ok() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(bookingRepository.findByBookerIdOrderByStartDesc(2L, PageRequest.of(0, 100)))
                .thenReturn(List.of(booking2));
        List<BookingOutDto> bookingOutDtoListCheck = bookingServiceRead.getStateBooker(2L, State.ALL, PageRequest.of(0, 100));
        assertThat(bookingOutDtoListCheck.get(0).getId(), equalTo(booking2.getId()));
        assertThat(bookingOutDtoListCheck.get(0).getEnd().toString(), equalTo(booking2.getEnd().toString()));
        assertThat(bookingOutDtoListCheck.get(0).getStart().toString(), equalTo(booking2.getStart().toString()));
        assertThat(bookingOutDtoListCheck.get(0).getItem().getId(), equalTo(booking2.getItem().getId()));
        assertThat(bookingOutDtoListCheck.get(0).getStatus(), equalTo(booking2.getStatus()));
        assertThat(bookingOutDtoListCheck.get(0).getBooker().getId(), equalTo(booking2.getBooker().getId()));
        verify(userRepository).findById(2L);
        verify(bookingRepository).findByBookerIdOrderByStartDesc(2L, PageRequest.of(0, 100));
    }

    @Test
    void getStateBookerFuture_ok() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(bookingRepository.findByBookerIdOrderByStartDesc(2L, PageRequest.of(0, 100)))
                .thenReturn(List.of(booking2));
        List<BookingOutDto> bookingOutDtoListCheck = bookingServiceRead.getStateBooker(2L, State.FUTURE, PageRequest.of(0, 100));
        assertThat(bookingOutDtoListCheck.get(0).getId(), equalTo(booking2.getId()));
        assertThat(bookingOutDtoListCheck.get(0).getEnd().toString(), equalTo(booking2.getEnd().toString()));
        assertThat(bookingOutDtoListCheck.get(0).getStart().toString(), equalTo(booking2.getStart().toString()));
        assertThat(bookingOutDtoListCheck.get(0).getItem().getId(), equalTo(booking2.getItem().getId()));
        assertThat(bookingOutDtoListCheck.get(0).getStatus(), equalTo(booking2.getStatus()));
        assertThat(bookingOutDtoListCheck.get(0).getBooker().getId(), equalTo(booking2.getBooker().getId()));
        verify(userRepository).findById(2L);
        verify(bookingRepository).findByBookerIdOrderByStartDesc(2L, PageRequest.of(0, 100));
    }

    @Test
    void getStateBookerCurrent_ok() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(bookingRepository.findByBookerIdOrderByStartDesc(2L, PageRequest.of(0, 100)))
                .thenReturn(List.of(booking3));
        List<BookingOutDto> bookingOutDtoListCheck = bookingServiceRead.getStateBooker(2L, State.CURRENT, PageRequest.of(0, 100));
        assertThat(bookingOutDtoListCheck.size(), equalTo(1));
        assertThat(bookingOutDtoListCheck.get(0).getId(), equalTo(booking3.getId()));
        assertThat(bookingOutDtoListCheck.get(0).getEnd().toString(), equalTo(booking3.getEnd().toString()));
        assertThat(bookingOutDtoListCheck.get(0).getStart().toString(), equalTo(booking3.getStart().toString()));
        assertThat(bookingOutDtoListCheck.get(0).getItem().getId(), equalTo(booking3.getItem().getId()));
        assertThat(bookingOutDtoListCheck.get(0).getStatus(), equalTo(booking3.getStatus()));
        assertThat(bookingOutDtoListCheck.get(0).getBooker().getId(), equalTo(booking3.getBooker().getId()));
        verify(userRepository).findById(2L);
        verify(bookingRepository).findByBookerIdOrderByStartDesc(2L, PageRequest.of(0, 100));
    }

    @Test
    void getStateBookerCurrentBookingInPast_error() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(bookingRepository.findByBookerIdOrderByStartDesc(2L, PageRequest.of(0, 100)))
                .thenReturn(List.of(booking1));
        List<BookingOutDto> bookingOutDtoListCheck = bookingServiceRead.getStateBooker(2L, State.CURRENT, PageRequest.of(0, 100));
        assertThat(bookingOutDtoListCheck.size(), equalTo(0));
        verify(userRepository).findById(2L);
        verify(bookingRepository).findByBookerIdOrderByStartDesc(2L, PageRequest.of(0, 100));
    }

    @Test
    void getStateBookerCurrentBookingInFuture_error() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(bookingRepository.findByBookerIdOrderByStartDesc(2L, PageRequest.of(0, 100)))
                .thenReturn(List.of(booking2));
        List<BookingOutDto> bookingOutDtoListCheck = bookingServiceRead.getStateBooker(2L, State.CURRENT, PageRequest.of(0, 100));
        assertThat(bookingOutDtoListCheck.size(), equalTo(0));
        verify(userRepository).findById(2L);
        verify(bookingRepository).findByBookerIdOrderByStartDesc(2L, PageRequest.of(0, 100));
    }

    @Test
    void getStateBookerPast_ok() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(bookingRepository.findByBookerIdOrderByStartDesc(2L, PageRequest.of(0, 100)))
                .thenReturn(List.of(booking1));
        List<BookingOutDto> bookingOutDtoListCheck = bookingServiceRead.getStateBooker(2L, State.PAST, PageRequest.of(0, 100));
        assertThat(bookingOutDtoListCheck.get(0).getId(), equalTo(booking1.getId()));
        assertThat(bookingOutDtoListCheck.get(0).getEnd().toString(), equalTo(booking1.getEnd().toString()));
        assertThat(bookingOutDtoListCheck.get(0).getStart().toString(), equalTo(booking1.getStart().toString()));
        assertThat(bookingOutDtoListCheck.get(0).getItem().getId(), equalTo(booking1.getItem().getId()));
        assertThat(bookingOutDtoListCheck.get(0).getStatus(), equalTo(booking1.getStatus()));
        assertThat(bookingOutDtoListCheck.get(0).getBooker().getId(), equalTo(booking1.getBooker().getId()));
        verify(userRepository).findById(2L);
        verify(bookingRepository).findByBookerIdOrderByStartDesc(2L, PageRequest.of(0, 100));
    }

    @Test
    void getStateBookerWaiting_ok() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(bookingRepository.findByBookerIdOrderByStartDesc(2L, PageRequest.of(0, 100)))
                .thenReturn(List.of(booking1));
        List<BookingOutDto> bookingOutDtoListCheck = bookingServiceRead.getStateBooker(2L, State.WAITING, PageRequest.of(0, 100));
        assertThat(bookingOutDtoListCheck.get(0).getId(), equalTo(booking1.getId()));
        assertThat(bookingOutDtoListCheck.get(0).getEnd().toString(), equalTo(booking1.getEnd().toString()));
        assertThat(bookingOutDtoListCheck.get(0).getStart().toString(), equalTo(booking1.getStart().toString()));
        assertThat(bookingOutDtoListCheck.get(0).getItem().getId(), equalTo(booking1.getItem().getId()));
        assertThat(bookingOutDtoListCheck.get(0).getStatus(), equalTo(booking1.getStatus()));
        assertThat(bookingOutDtoListCheck.get(0).getBooker().getId(), equalTo(booking1.getBooker().getId()));
        verify(userRepository).findById(2L);
        verify(bookingRepository).findByBookerIdOrderByStartDesc(2L, PageRequest.of(0, 100));
    }

    @Test
    void getStateBookerRejected_ok() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(bookingRepository.findByBookerIdOrderByStartDesc(2L, PageRequest.of(0, 100)))
                .thenReturn(List.of(booking3));
        List<BookingOutDto> bookingOutDtoListCheck = bookingServiceRead.getStateBooker(2L, State.REJECTED, PageRequest.of(0, 100));
        assertThat(bookingOutDtoListCheck.get(0).getId(), equalTo(booking3.getId()));
        assertThat(bookingOutDtoListCheck.get(0).getEnd().toString(), equalTo(booking3.getEnd().toString()));
        assertThat(bookingOutDtoListCheck.get(0).getStart().toString(), equalTo(booking3.getStart().toString()));
        assertThat(bookingOutDtoListCheck.get(0).getItem().getId(), equalTo(booking3.getItem().getId()));
        assertThat(bookingOutDtoListCheck.get(0).getStatus(), equalTo(booking3.getStatus()));
        assertThat(bookingOutDtoListCheck.get(0).getBooker().getId(), equalTo(booking3.getBooker().getId()));
        verify(userRepository).findById(2L);
        verify(bookingRepository).findByBookerIdOrderByStartDesc(2L, PageRequest.of(0, 100));
    }

    @Test
    void getStateOwnerAll_ok() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(1L, PageRequest.of(0, 100)))
                .thenReturn(List.of(booking2));
        List<BookingOutDto> bookingOutDtoListCheck = bookingServiceRead.getStateOwner(1L, State.ALL, PageRequest.of(0, 100));
        assertThat(bookingOutDtoListCheck.get(0).getId(), equalTo(booking2.getId()));
        assertThat(bookingOutDtoListCheck.get(0).getEnd().toString(), equalTo(booking2.getEnd().toString()));
        assertThat(bookingOutDtoListCheck.get(0).getStart().toString(), equalTo(booking2.getStart().toString()));
        assertThat(bookingOutDtoListCheck.get(0).getItem().getId(), equalTo(booking2.getItem().getId()));
        assertThat(bookingOutDtoListCheck.get(0).getStatus(), equalTo(booking2.getStatus()));
        assertThat(bookingOutDtoListCheck.get(0).getBooker().getId(), equalTo(booking2.getBooker().getId()));
        verify(userRepository).findById(1L);
        verify(bookingRepository).findByItemOwnerIdOrderByStartDesc(1L, PageRequest.of(0, 100));
    }

    @Test
    void getStateOwnerFuture_ok() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(1L, PageRequest.of(0, 100)))
                .thenReturn(List.of(booking2));
        List<BookingOutDto> bookingOutDtoListCheck = bookingServiceRead.getStateOwner(1L, State.FUTURE, PageRequest.of(0, 100));
        assertThat(bookingOutDtoListCheck.get(0).getId(), equalTo(booking2.getId()));
        assertThat(bookingOutDtoListCheck.get(0).getEnd().toString(), equalTo(booking2.getEnd().toString()));
        assertThat(bookingOutDtoListCheck.get(0).getStart().toString(), equalTo(booking2.getStart().toString()));
        assertThat(bookingOutDtoListCheck.get(0).getItem().getId(), equalTo(booking2.getItem().getId()));
        assertThat(bookingOutDtoListCheck.get(0).getStatus(), equalTo(booking2.getStatus()));
        assertThat(bookingOutDtoListCheck.get(0).getBooker().getId(), equalTo(booking2.getBooker().getId()));
        verify(userRepository).findById(1L);
        verify(bookingRepository).findByItemOwnerIdOrderByStartDesc(1L, PageRequest.of(0, 100));
    }

    @Test
    void getStateOwnerCurrent_ok() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(1L, PageRequest.of(0, 100)))
                .thenReturn(List.of(booking3));
        List<BookingOutDto> bookingOutDtoListCheck = bookingServiceRead.getStateOwner(1L, State.CURRENT, PageRequest.of(0, 100));
        assertThat(bookingOutDtoListCheck.get(0).getId(), equalTo(booking3.getId()));
        assertThat(bookingOutDtoListCheck.get(0).getEnd().toString(), equalTo(booking3.getEnd().toString()));
        assertThat(bookingOutDtoListCheck.get(0).getStart().toString(), equalTo(booking3.getStart().toString()));
        assertThat(bookingOutDtoListCheck.get(0).getItem().getId(), equalTo(booking3.getItem().getId()));
        assertThat(bookingOutDtoListCheck.get(0).getStatus(), equalTo(booking3.getStatus()));
        assertThat(bookingOutDtoListCheck.get(0).getBooker().getId(), equalTo(booking3.getBooker().getId()));
        verify(userRepository).findById(1L);
        verify(bookingRepository).findByItemOwnerIdOrderByStartDesc(1L, PageRequest.of(0, 100));
    }

    @Test
    void getStateOwnerCurrentBookingInPast_error() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(1L, PageRequest.of(0, 100)))
                .thenReturn(List.of(booking1));
        List<BookingOutDto> bookingOutDtoListCheck = bookingServiceRead.getStateOwner(1L, State.CURRENT, PageRequest.of(0, 100));
        assertThat(bookingOutDtoListCheck.size(), equalTo(0));
        verify(userRepository).findById(1L);
        verify(bookingRepository).findByItemOwnerIdOrderByStartDesc(1L, PageRequest.of(0, 100));
    }

    @Test
    void getStateOwnerCurrentBookingInFuture_error() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(1L, PageRequest.of(0, 100)))
                .thenReturn(List.of(booking2));
        List<BookingOutDto> bookingOutDtoListCheck = bookingServiceRead.getStateOwner(1L, State.CURRENT, PageRequest.of(0, 100));
        assertThat(bookingOutDtoListCheck.size(), equalTo(0));
        verify(userRepository).findById(1L);
        verify(bookingRepository).findByItemOwnerIdOrderByStartDesc(1L, PageRequest.of(0, 100));
    }

    @Test
    void getStateOwnerRejected_ok() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(1L, PageRequest.of(0, 100)))
                .thenReturn(List.of(booking3));
        List<BookingOutDto> bookingOutDtoListCheck = bookingServiceRead.getStateOwner(1L, State.REJECTED, PageRequest.of(0, 100));
        assertThat(bookingOutDtoListCheck.get(0).getId(), equalTo(booking3.getId()));
        assertThat(bookingOutDtoListCheck.get(0).getEnd().toString(), equalTo(booking3.getEnd().toString()));
        assertThat(bookingOutDtoListCheck.get(0).getStart().toString(), equalTo(booking3.getStart().toString()));
        assertThat(bookingOutDtoListCheck.get(0).getItem().getId(), equalTo(booking3.getItem().getId()));
        assertThat(bookingOutDtoListCheck.get(0).getStatus(), equalTo(booking3.getStatus()));
        assertThat(bookingOutDtoListCheck.get(0).getBooker().getId(), equalTo(booking3.getBooker().getId()));
        verify(userRepository).findById(1L);
        verify(bookingRepository).findByItemOwnerIdOrderByStartDesc(1L, PageRequest.of(0, 100));
    }

    @Test
    void getStateOwnerWaiting_ok() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(1L, PageRequest.of(0, 100)))
                .thenReturn(List.of(booking1));
        List<BookingOutDto> bookingOutDtoListCheck = bookingServiceRead.getStateOwner(1L, State.WAITING, PageRequest.of(0, 100));
        assertThat(bookingOutDtoListCheck.get(0).getId(), equalTo(booking1.getId()));
        assertThat(bookingOutDtoListCheck.get(0).getEnd().toString(), equalTo(booking1.getEnd().toString()));
        assertThat(bookingOutDtoListCheck.get(0).getStart().toString(), equalTo(booking1.getStart().toString()));
        assertThat(bookingOutDtoListCheck.get(0).getItem().getId(), equalTo(booking1.getItem().getId()));
        assertThat(bookingOutDtoListCheck.get(0).getStatus(), equalTo(booking1.getStatus()));
        assertThat(bookingOutDtoListCheck.get(0).getBooker().getId(), equalTo(booking1.getBooker().getId()));
        verify(userRepository).findById(1L);
        verify(bookingRepository).findByItemOwnerIdOrderByStartDesc(1L, PageRequest.of(0, 100));
    }

    @Test
    void getStateOwnerPast_ok() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(1L, PageRequest.of(0, 100)))
                .thenReturn(List.of(booking1));
        List<BookingOutDto> bookingOutDtoListCheck = bookingServiceRead.getStateOwner(1L, State.PAST, PageRequest.of(0, 100));
        assertThat(bookingOutDtoListCheck.get(0).getId(), equalTo(booking1.getId()));
        assertThat(bookingOutDtoListCheck.get(0).getEnd().toString(), equalTo(booking1.getEnd().toString()));
        assertThat(bookingOutDtoListCheck.get(0).getStart().toString(), equalTo(booking1.getStart().toString()));
        assertThat(bookingOutDtoListCheck.get(0).getItem().getId(), equalTo(booking1.getItem().getId()));
        assertThat(bookingOutDtoListCheck.get(0).getStatus(), equalTo(booking1.getStatus()));
        assertThat(bookingOutDtoListCheck.get(0).getBooker().getId(), equalTo(booking1.getBooker().getId()));
        verify(userRepository).findById(1L);
        verify(bookingRepository).findByItemOwnerIdOrderByStartDesc(1L, PageRequest.of(0, 100));
    }
}