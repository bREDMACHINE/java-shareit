package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutDto;

import java.util.List;

public interface ItemServiceRead {

    ItemOutDto getItem(long userId, long itemId);

    List<ItemOutDto> findAllItems(long userId);

    List<ItemDto> searchItems(long userId, String text);
}
