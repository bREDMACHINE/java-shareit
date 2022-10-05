package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

public interface ItemServiceUpdate {

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);
}
