package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

public interface BookingServiceCreate {

    BookingDto createBooking(long userId, BookingDto bookingDto);
}
