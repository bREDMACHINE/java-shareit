package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Creating item {}, userId={}", itemDto, userId);
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId, @RequestBody ItemDto itemDto) {
        log.info("Patch item {}, userId={}", itemId, userId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.info("Get item {}, userId={}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get items with userId={}", userId);
        return itemClient.findAllItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam String text) {
        log.info("Get items with userId={}, text={}", userId, text);
        return itemClient.searchItems(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId, @Valid @RequestBody CommentDto commentDto) {
        log.info("Creating comment{}, userId={}", commentDto, userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
