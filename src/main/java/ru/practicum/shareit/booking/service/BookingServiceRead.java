package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingOutDto;

import java.util.List;

public interface BookingServiceRead {

    BookingOutDto getStatus(long userId, long bookingId);

    List<BookingOutDto> getStateBooker(long userId, String state, Pageable pageable);

    List<BookingOutDto> getStateOwner(long userId, String state, Pageable pageable);
}
