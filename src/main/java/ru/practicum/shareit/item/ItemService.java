package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    ItemOutDto getItem(long userId, long itemId);

    List<ItemOutDto> findAllItems(long userId);

    List<ItemDto> searchItems(long userId, String text);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);
}
