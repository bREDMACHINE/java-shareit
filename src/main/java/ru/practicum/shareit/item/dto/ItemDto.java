package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {

    public interface New {
    }

    @Null(groups = {New.class})
    Long id;
    @NotBlank(groups = {New.class})
    String name;
    @NotBlank(groups = {New.class})
    String description;
    @NotNull(groups = {New.class})
    Boolean available;
    User owner;
    ItemRequest request;
}
