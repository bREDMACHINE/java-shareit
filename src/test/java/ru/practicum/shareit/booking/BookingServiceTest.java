package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
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
class BookingServiceTest {

    private BookingService bookingService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
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
    private final Booking booking2 = new Booking(2L, LocalDateTime.of(2022, 9, 29, 19, 15,15),
            LocalDateTime.of(2022, 9, 29, 20, 15,15), item1, user2, Status.APPROVED);
    private final Booking booking3 = new Booking(2L, LocalDateTime.of(2022, 9, 20, 19, 15,15),
            LocalDateTime.of(2022, 9, 24, 20, 15,15), item2, user2, Status.REJECTED);
    private final BookingDto bookingDto1 = new BookingDto(1L, LocalDateTime.of(2022, 9, 18, 19, 15,15),
            LocalDateTime.of(2022, 9, 18, 20, 15,15), 1L);
    private final BookingDto bookingDto3 = new BookingDto(2L, LocalDateTime.of(2022, 9, 20, 19, 15,15),
            LocalDateTime.of(2022, 9, 24, 20, 15,15), 2L);
    private final BookingDto bookingDto4 = new BookingDto(2L, LocalDateTime.of(2022, 10, 20, 19, 15,15),
            LocalDateTime.of(2022, 9, 24, 20, 15,15), 1L);
    private final Booking updateBooking1 = new Booking(1L, LocalDateTime.of(2022, 9, 18, 19, 15,15),
            LocalDateTime.of(2022, 9, 18, 20, 15,15), item1, user2, Status.APPROVED);
    private final Booking updateBooking = new Booking(1L, LocalDateTime.of(2022, 9, 18, 19, 15,15),
            LocalDateTime.of(2022, 9, 18, 20, 15,15), item1, user2, Status.REJECTED);

    @Test
    void createBooking_ok() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(bookingRepository.save(booking1)).thenReturn(booking1);
        BookingDto bookingDtoCheck = bookingService.createBooking(2L, bookingDto1);
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
                () -> bookingService.createBooking(100L, bookingDto1));
        Assertions.assertEquals("Указанный userId не существует", exception.getMessage());
        verify(userRepository).findById(100L);
    }

    @Test
    void createBookingNotFoundFromOwner_error() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.createBooking(1L, bookingDto1));
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
                () -> bookingService.createBooking(1L, bookingDto3));
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
                () -> bookingService.createBooking(1L, bookingDto4));
        Assertions.assertEquals("Неправильный запрос на бронирование", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(itemRepository).findById(1L);
    }

    @Test
    void updateStatusApprove_ok() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking1));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(bookingRepository.save(updateBooking1)).thenReturn(updateBooking1);
        BookingOutDto bookingOutDtoCheck = bookingService.updateStatus(1L, 1L, true);
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
        BookingOutDto bookingOutDtoCheck = bookingService.updateStatus(1L, 1L, false);
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
                () -> bookingService.updateStatus(1L, 2L, true));
        Assertions.assertEquals("Невозможно повторное изменение статуса", exception.getMessage());
        verify(bookingRepository).findById(2L);
    }

    @Test
    void updateStatusNotFoundNotOwner_error() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.updateStatus(2L, 1L, true));
        Assertions.assertEquals("userId не является владельцем, недоступно изменение параметров", exception.getMessage());
        verify(userRepository).findById(2L);
        verify(bookingRepository).findById(1L);
    }

    @Test
    void getStatusOwner_ok() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(bookingRepository.findById(2L)).thenReturn(Optional.of(booking2));
        BookingOutDto bookingOutDtoCheck = bookingService.getStatus(1L, 2L);
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
        BookingOutDto bookingOutDtoCheck = bookingService.getStatus(2L, 2L);
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
                () -> bookingService.getStatus(3L, 2L));
        Assertions.assertEquals("userId не является владельцем или автором брони", exception.getMessage());
        verify(userRepository).findById(3L);
        verify(bookingRepository).findById(2L);
    }

    @Test
    void getStateBookerAll_ok() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(bookingRepository.findByBookerIdOrderByStartDesc(2L, PageRequest.of(0, 100)))
                .thenReturn(List.of(booking2));
        List<BookingOutDto> bookingOutDtoListCheck = bookingService.getStateBooker(2L, State.ALL, PageRequest.of(0, 100));
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
        List<BookingOutDto> bookingOutDtoListCheck = bookingService.getStateBooker(2L, State.FUTURE, PageRequest.of(0, 100));
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
        List<BookingOutDto> bookingOutDtoListCheck = bookingService.getStateBooker(2L, State.CURRENT, PageRequest.of(0, 100));
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
        List<BookingOutDto> bookingOutDtoListCheck = bookingService.getStateBooker(2L, State.CURRENT, PageRequest.of(0, 100));
        assertThat(bookingOutDtoListCheck.size(), equalTo(0));
        verify(userRepository).findById(2L);
        verify(bookingRepository).findByBookerIdOrderByStartDesc(2L, PageRequest.of(0, 100));
    }

    @Test
    void getStateBookerCurrentBookingInFuture_error() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(bookingRepository.findByBookerIdOrderByStartDesc(2L, PageRequest.of(0, 100)))
                .thenReturn(List.of(booking2));
        List<BookingOutDto> bookingOutDtoListCheck = bookingService.getStateBooker(2L, State.CURRENT, PageRequest.of(0, 100));
        assertThat(bookingOutDtoListCheck.size(), equalTo(0));
        verify(userRepository).findById(2L);
        verify(bookingRepository).findByBookerIdOrderByStartDesc(2L, PageRequest.of(0, 100));
    }

    @Test
    void getStateBookerPast_ok() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(bookingRepository.findByBookerIdOrderByStartDesc(2L, PageRequest.of(0, 100)))
                .thenReturn(List.of(booking1));
        List<BookingOutDto> bookingOutDtoListCheck = bookingService.getStateBooker(2L, State.PAST, PageRequest.of(0, 100));
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
        List<BookingOutDto> bookingOutDtoListCheck = bookingService.getStateBooker(2L, State.WAITING, PageRequest.of(0, 100));
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
        List<BookingOutDto> bookingOutDtoListCheck = bookingService.getStateBooker(2L, State.REJECTED, PageRequest.of(0, 100));
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
        List<BookingOutDto> bookingOutDtoListCheck = bookingService.getStateOwner(1L, State.ALL, PageRequest.of(0, 100));
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
        List<BookingOutDto> bookingOutDtoListCheck = bookingService.getStateOwner(1L, State.FUTURE, PageRequest.of(0, 100));
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
        List<BookingOutDto> bookingOutDtoListCheck = bookingService.getStateOwner(1L, State.CURRENT, PageRequest.of(0, 100));
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
        List<BookingOutDto> bookingOutDtoListCheck = bookingService.getStateOwner(1L, State.CURRENT, PageRequest.of(0, 100));
        assertThat(bookingOutDtoListCheck.size(), equalTo(0));
        verify(userRepository).findById(1L);
        verify(bookingRepository).findByItemOwnerIdOrderByStartDesc(1L, PageRequest.of(0, 100));
    }

    @Test
    void getStateOwnerCurrentBookingInFuture_error() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(1L, PageRequest.of(0, 100)))
                .thenReturn(List.of(booking2));
        List<BookingOutDto> bookingOutDtoListCheck = bookingService.getStateOwner(1L, State.CURRENT, PageRequest.of(0, 100));
        assertThat(bookingOutDtoListCheck.size(), equalTo(0));
        verify(userRepository).findById(1L);
        verify(bookingRepository).findByItemOwnerIdOrderByStartDesc(1L, PageRequest.of(0, 100));
    }

    @Test
    void getStateOwnerRejected_ok() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(1L, PageRequest.of(0, 100)))
                .thenReturn(List.of(booking3));
        List<BookingOutDto> bookingOutDtoListCheck = bookingService.getStateOwner(1L, State.REJECTED, PageRequest.of(0, 100));
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
        List<BookingOutDto> bookingOutDtoListCheck = bookingService.getStateOwner(1L, State.WAITING, PageRequest.of(0, 100));
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
        List<BookingOutDto> bookingOutDtoListCheck = bookingService.getStateOwner(1L, State.PAST, PageRequest.of(0, 100));
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