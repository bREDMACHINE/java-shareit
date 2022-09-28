package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

public interface ItemRequestServiceCreate {

    ItemRequestDto addItemRequest(long userId, ItemRequestDto itemRequestDto);
}
