package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.item.service.ItemServiceCreate;
import ru.practicum.shareit.item.service.ItemServiceRead;
import ru.practicum.shareit.item.service.ItemServiceUpdate;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class ItemController {

    private final ItemServiceCreate itemServiceCreate;
    private final ItemServiceUpdate itemServiceUpdate;
    private final ItemServiceRead itemServiceRead;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody ItemDto itemDto) {
        log.info("Получен Post запрос к эндпоинту /items");
        return itemServiceCreate.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId, @RequestBody ItemDto itemDto) {
        log.info("Получен Patch запрос к эндпоинту /items/{}", itemId);
        return itemServiceUpdate.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemOutDto getItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.info("Получен Get запрос к эндпоинту /items/{}", itemId);
        return itemServiceRead.getItem(userId, itemId);
    }

    @GetMapping
    public List<ItemOutDto> findAllItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен Get запрос к эндпоинту /items");
        return itemServiceRead.findAllItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam String text) {
        log.info("Получен Get запрос к эндпоинту /items/search");
        return itemServiceRead.searchItems(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId, @RequestBody CommentDto commentDto) {
        log.info("Получен Post запрос к эндпоинту /{}/comment", itemId);
        return itemServiceCreate.addComment(userId, itemId, commentDto);
    }
}
