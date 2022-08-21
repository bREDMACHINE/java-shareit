package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@AllArgsConstructor
public class ItemDto {

    public interface New {
    }

    @Null(groups = {New.class})
    private Long id;
    @NotBlank(groups = {New.class})
    @NotEmpty(groups = {New.class})
    private String name;
    @NotBlank(groups = {New.class})
    @NotEmpty(groups = {New.class})
    private String description;
    @NotNull(groups = {New.class})
    private Boolean available;
    private User owner;
    private ItemRequest request;
}
