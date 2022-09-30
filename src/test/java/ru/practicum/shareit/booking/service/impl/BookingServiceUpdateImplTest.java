package ru.practicum.shareit.booking.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.service.BookingServiceUpdate;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceUpdateImplTest {

    private BookingServiceUpdate bookingServiceUpdate;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;

    @BeforeEach
    void setUp() {
        bookingServiceUpdate = new BookingServiceUpdateImpl(bookingRepository, userRepository);
    }

    private final User user1 = new User(1L, "nameUser1", "email@User1");
    private final User user2 = new User(2L, "nameUser2", "email@User2");
    private final ItemRequest itemRequest1 = new ItemRequest(1L,
            "DescriptionItemRequest1", user2,
            LocalDateTime.of(2022, 9, 18, 18, 15,15));
    private final Item item1 = new Item(1L, "nameItem1", "descriptionItem1", true, user1, itemRequest1);
    private final Booking booking1 = new Booking(1L, LocalDateTime.of(2022, 9, 18, 19, 15,15),
            LocalDateTime.of(2022, 9, 18, 20, 15,15), item1, user2, Status.WAITING);
    private final Booking booking2 = new Booking(2L, LocalDateTime.of(2022, 9, 29, 19, 15,15),
            LocalDateTime.of(2022, 9, 29, 20, 15,15), item1, user2, Status.APPROVED);
    private final Booking updateBooking1 = new Booking(1L, LocalDateTime.of(2022, 9, 18, 19, 15,15),
            LocalDateTime.of(2022, 9, 18, 20, 15,15), item1, user2, Status.APPROVED);
    private final Booking updateBooking = new Booking(1L, LocalDateTime.of(2022, 9, 18, 19, 15,15),
            LocalDateTime.of(2022, 9, 18, 20, 15,15), item1, user2, Status.REJECTED);

    @Test
    void updateStatusApprove_ok() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking1));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(bookingRepository.save(updateBooking1)).thenReturn(updateBooking1);
        BookingOutDto bookingOutDtoCheck = bookingServiceUpdate.updateStatus(1L, 1L, true);
        assertThat(bookingOutDtoCheck.getId(), equalTo(updateBooking1.getId()));
        assertThat(bookingOutDtoCheck.getEnd().toString(), equalTo(updateBooking1.getEnd().toString()));
        assertThat(bookingOutDtoCheck.getStart().toString(), equalTo(updateBooking1.getStart().toString()));
        assertThat(bookingOutDtoCheck.getItem().getId(), equalTo(updateBooking1.getItem().getId()));
        assertThat(bookingOutDtoCheck.getStatus(), equalTo(updateBooking1.getStatus()));
        assertThat(bookingOutDtoCheck.getBooker().getId(), equalTo(updateBooking1.getBooker().getId()));
        verify(userRepository).findById(1L);
        verify(bookingRepository).findById(1L);
        verify(bookingRepository).save(updateBooking1);
    }

    @Test
    void updateStatusReject_ok() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking1));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(bookingRepository.save(updateBooking)).thenReturn(updateBooking);
        BookingOutDto bookingOutDtoCheck = bookingServiceUpdate.updateStatus(1L, 1L, false);
        assertThat(bookingOutDtoCheck.getId(), equalTo(updateBooking.getId()));
        assertThat(bookingOutDtoCheck.getEnd().toString(), equalTo(updateBooking.getEnd().toString()));
        assertThat(bookingOutDtoCheck.getStart().toString(), equalTo(updateBooking.getStart().toString()));
        assertThat(bookingOutDtoCheck.getItem().getId(), equalTo(updateBooking.getItem().getId()));
        assertThat(bookingOutDtoCheck.getStatus(), equalTo(updateBooking.getStatus()));
        assertThat(bookingOutDtoCheck.getBooker().getId(), equalTo(updateBooking.getBooker().getId()));
        verify(userRepository).findById(1L);
        verify(bookingRepository).findById(1L);
        verify(bookingRepository).save(updateBooking);
    }

    @Test
    void updateStatusBadRequestAllReadyApproved_error() {
        when(bookingRepository.findById(2L)).thenReturn(Optional.of(booking2));
        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingServiceUpdate.updateStatus(1L, 2L, true));
        Assertions.assertEquals("Невозможно повторное изменение статуса", exception.getMessage());
        verify(bookingRepository).findById(2L);
    }

    @Test
    void updateStatusNotFoundNotOwner_error() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingServiceUpdate.updateStatus(2L, 1L, true));
        Assertions.assertEquals("userId не является владельцем, недоступно изменение параметров", exception.getMessage());
        verify(userRepository).findById(2L);
        verify(bookingRepository).findById(1L);
    }
}