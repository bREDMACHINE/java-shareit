package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

public interface ItemServiceCreate {

    ItemDto addItem(Long userId, ItemDto itemDto);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);
}
