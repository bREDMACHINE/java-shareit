package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable());
    }

    public static Item toItem(ItemDto itemDto, User user) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), user, null);
    }

    public static ItemOutDto toItemOutDto(Item item, Booking lastBooking, Booking nextBooking, List<CommentDto> comments) {
        return new ItemOutDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), lastBooking != null ? new ItemOutDto.BookingForItemOutDto(lastBooking.getId(), lastBooking.getBooker().getId()) : null, nextBooking != null ? new ItemOutDto.BookingForItemOutDto(nextBooking.getId(), nextBooking.getBooker().getId()) : null, comments);
    }

    public static ItemOutDto toItemOutDto(Item item, List<CommentDto> comments) {
        return new ItemOutDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), null, null, comments);
    }
}
