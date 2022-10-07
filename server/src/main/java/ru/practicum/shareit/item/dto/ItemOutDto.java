package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemOutDto {

    Long id;
    String name;
    String description;
    Boolean available;
    BookingForItemOutDto lastBooking;
    BookingForItemOutDto nextBooking;
    List<CommentDto> comments = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class BookingForItemOutDto {
        Long id;
        Long bookerId;
    }
}
