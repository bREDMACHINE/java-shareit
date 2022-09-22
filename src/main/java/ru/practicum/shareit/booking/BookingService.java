package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(long userId, BookingDto bookingDto);

    BookingOutDto updateStatus(long userId, long bookingId, boolean approved);

    BookingOutDto getStatus(long userId, long bookingId);

    List<BookingOutDto> getStateBooker(long userId, State state, Pageable pageable);

    List<BookingOutDto> getStateOwner(long userId, State state, Pageable pageable);
}
