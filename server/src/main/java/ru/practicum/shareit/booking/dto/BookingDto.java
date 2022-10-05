package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDto {

    Long id;
    @DateTimeFormat(pattern = "YYYY-MM-DDTHH:mm:ss")
    LocalDateTime start;
    @DateTimeFormat(pattern = "YYYY-MM-DDTHH:mm:ss")
    LocalDateTime end;
    Long itemId;
}
