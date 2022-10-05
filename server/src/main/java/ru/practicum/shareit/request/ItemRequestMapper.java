package ru.practicum.shareit.request;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user) {
        return new ItemRequest(itemRequestDto.getId(),
                itemRequestDto.getDescription(), user, itemRequestDto.getCreated() == null ? LocalDateTime.now() : itemRequestDto.getCreated());
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getRequester().getId(),
                itemRequest.getCreated());
    }

    public static ItemRequestOutDto toItemRequestOutDto(ItemRequest itemRequest, List<ItemDto> items) {
        return new ItemRequestOutDto(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getRequester().getId(),
                itemRequest.getCreated(), items);
    }
}
