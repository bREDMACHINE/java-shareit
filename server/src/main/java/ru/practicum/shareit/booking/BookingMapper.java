package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;


public class BookingMapper {
    public static Booking toBooking(BookingDto bookingDto, Item item, User user, Status status) {
        return new Booking(bookingDto.getId(), bookingDto.getStart(), bookingDto.getEnd(), item, user, status);
    }

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getItem().getId());
    }

    public static BookingOutDto toBookingOutDto(Booking booking) {
        return new BookingOutDto(booking.getId(), booking.getStart(), booking.getEnd(), new BookingOutDto.ItemForBookingOutDto(booking.getItem().getId(), booking.getItem().getName()), new BookingOutDto.UserForBookingOutDto(booking.getBooker().getId()), booking.getStatus());
    }
}
