package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingOutDto;

public interface BookingServiceUpdate {

    BookingOutDto updateStatus(long userId, long bookingId, boolean approved);
}
