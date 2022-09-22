package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public BookingDto createBooking(long userId, BookingDto bookingDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Указанный userId не существует"));
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new NotFoundException("Указанный itemId не существует"));

        if (item.getAvailable() && bookingDto.getStart().isBefore(bookingDto.getEnd())) {
            if (user.getId().equals(item.getOwner().getId())) {
                throw new NotFoundException("userId является владельцем");
            }
            Booking booking = BookingMapper.toBooking(bookingDto, item, user, Status.WAITING);
            return BookingMapper.toBookingDto(bookingRepository.save(booking));
        } else {
            throw new BadRequestException("Неправильный запрос на бронирование");
        }
    }

    @Transactional
    @Override
    public BookingOutDto updateStatus(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Указанный bookingId не существует"));
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new BadRequestException("Невозможно повторное изменение статуса");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Указанный userId не существует"));
        if (Objects.equals(booking.getItem().getOwner().getId(), user.getId())) {
            if (approved) {
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(Status.REJECTED);
            }
            return BookingMapper.toBookingOutDto(bookingRepository.save(booking));
        } else {
            throw new NotFoundException("userId не является владельцем, недоступно изменение параметров");
        }
    }

    @Override
    public BookingOutDto getStatus(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Указанный bookingId не существует"));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Указанный userId не существует"));
        if (Objects.equals(user.getId(), booking.getBooker().getId()) || Objects.equals(user.getId(), booking.getItem().getOwner().getId())) {
            return BookingMapper.toBookingOutDto(booking);
        } else {
            throw new NotFoundException("userId не является владельцем или автором брони");
        }
    }

    @Override
    public List<BookingOutDto> getStateBooker(long userId, State state, Pageable pageable) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Указанный userId не существует"));
        switch (state) {
            case ALL:
                return bookingRepository.findByBookerIdOrderByStartDesc(user.getId(), pageable).stream()
                        .map(BookingMapper::toBookingOutDto)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findByBookerIdOrderByStartDesc(user.getId(), pageable).stream()
                        .filter(booking -> booking.getEnd().isAfter(LocalDateTime.now()) && booking.getStart().isBefore(LocalDateTime.now()))
                        .map(BookingMapper::toBookingOutDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findByBookerIdOrderByStartDesc(user.getId(), pageable).stream()
                        .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                        .map(BookingMapper::toBookingOutDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findByBookerIdOrderByStartDesc(user.getId(), pageable).stream()
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                        .map(BookingMapper::toBookingOutDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findByBookerIdOrderByStartDesc(user.getId(), pageable).stream()
                        .filter(booking -> booking.getStatus().equals(Status.WAITING))
                        .map(BookingMapper::toBookingOutDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findByBookerIdOrderByStartDesc(user.getId(), pageable).stream()
                        .filter(booking -> booking.getStatus().equals(Status.REJECTED))
                        .map(BookingMapper::toBookingOutDto)
                        .collect(Collectors.toList());
            default:
                return null;
        }
    }

    @Override
    public List<BookingOutDto> getStateOwner(long userId, State state, Pageable pageable) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Указанный userId не существует"));
        switch (state) {
            case ALL:
                return bookingRepository.findByItemOwnerIdOrderByStartDesc(user.getId(), pageable).stream().map(BookingMapper::toBookingOutDto).collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findByItemOwnerIdOrderByStartDesc(user.getId(), pageable).stream().filter(booking -> booking.getEnd().isAfter(LocalDateTime.now()) && booking.getStart().isBefore(LocalDateTime.now())).map(BookingMapper::toBookingOutDto).collect(Collectors.toList());
            case PAST:
                return bookingRepository.findByItemOwnerIdOrderByStartDesc(user.getId(), pageable).stream().filter(booking -> booking.getEnd().isBefore(LocalDateTime.now())).map(BookingMapper::toBookingOutDto).collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findByItemOwnerIdOrderByStartDesc(user.getId(), pageable).stream().filter(booking -> booking.getStart().isAfter(LocalDateTime.now())).map(BookingMapper::toBookingOutDto).collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findByItemOwnerIdOrderByStartDesc(user.getId(), pageable).stream().filter(booking -> booking.getStatus().equals(Status.WAITING)).map(BookingMapper::toBookingOutDto).collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findByItemOwnerIdOrderByStartDesc(user.getId(), pageable).stream().filter(booking -> booking.getStatus().equals(Status.REJECTED)).map(BookingMapper::toBookingOutDto).collect(Collectors.toList());
            default:
                return null;
        }
    }
}
