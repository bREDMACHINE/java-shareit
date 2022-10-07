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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingServiceCreate;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
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
class BookingServiceCreateImplTest {

    private BookingServiceCreate bookingServiceCreate;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;

    @BeforeEach
    void setUp() {
        bookingServiceCreate = new BookingServiceCreateImpl(bookingRepository, userRepository, itemRepository);
    }

    private final User user1 = new User(1L, "nameUser1", "email@User1");
    private final User user2 = new User(2L, "nameUser2", "email@User2");
    private final ItemRequest itemRequest1 = new ItemRequest(1L,
            "DescriptionItemRequest1", user2,
            LocalDateTime.of(2022, 9, 18, 18, 15,15));
    private final Item item1 = new Item(1L, "nameItem1", "descriptionItem1", true, user1, itemRequest1);
    private final Item item2 = new Item(2L, "nameItem2", "descriptionItem2", false, user1, null);
    private final Booking booking1 = new Booking(1L, LocalDateTime.of(2022, 9, 18, 19, 15,15),
            LocalDateTime.of(2022, 9, 18, 20, 15,15), item1, user2, Status.WAITING);
    private final BookingDto bookingDto1 = new BookingDto(1L, LocalDateTime.of(2022, 9, 18, 19, 15,15),
            LocalDateTime.of(2022, 9, 18, 20, 15,15), 1L);
    private final BookingDto bookingDto3 = new BookingDto(2L, LocalDateTime.of(2022, 9, 20, 19, 15,15),
            LocalDateTime.of(2022, 9, 24, 20, 15,15), 2L);
    private final BookingDto bookingDto4 = new BookingDto(2L, LocalDateTime.of(2022, 10, 20, 19, 15,15),
            LocalDateTime.of(2022, 9, 24, 20, 15,15), 1L);

    @Test
    void createBooking_ok() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(bookingRepository.save(booking1)).thenReturn(booking1);
        BookingDto bookingDtoCheck = bookingServiceCreate.createBooking(2L, bookingDto1);
        assertThat(bookingDtoCheck.getId(), equalTo(bookingDto1.getId()));
        assertThat(bookingDtoCheck.getEnd().toString(), equalTo(bookingDto1.getEnd().toString()));
        assertThat(bookingDtoCheck.getStart().toString(), equalTo(bookingDto1.getStart().toString()));
        assertThat(bookingDtoCheck.getItemId(), equalTo(bookingDto1.getItemId()));
        verify(userRepository).findById(2L);
        verify(itemRepository).findById(1L);
        verify(bookingRepository).save(booking1);
    }

    @Test
    void createBookingNotFoundUser_error() {
        when(userRepository.findById(100L)).thenReturn(Optional.empty());
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingServiceCreate.createBooking(100L, bookingDto1));
        Assertions.assertEquals("Указанный userId не существует", exception.getMessage());
        verify(userRepository).findById(100L);
    }

    @Test
    void createBookingNotFoundFromOwner_error() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingServiceCreate.createBooking(1L, bookingDto1));
        Assertions.assertEquals("userId является владельцем", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(itemRepository).findById(1L);
    }

    @Test
    void createBookingBadRequestItemNotAvailable_error() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item2));
        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingServiceCreate.createBooking(1L, bookingDto3));
        Assertions.assertEquals("Неправильный запрос на бронирование", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(itemRepository).findById(2L);
    }

    @Test
    void createBookingBadRequestBadBookingTimeSet_error() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingServiceCreate.createBooking(1L, bookingDto4));
        Assertions.assertEquals("Неправильный запрос на бронирование", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(itemRepository).findById(1L);
    }
}