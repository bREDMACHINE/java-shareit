package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {
    Long id;
    String text;
    String authorName;
    @DateTimeFormat(pattern = "YYYY-MM-DDTHH:mm:ss")
    LocalDateTime created;
}
