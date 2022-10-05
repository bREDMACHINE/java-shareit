package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Creating request {}, userId={}", itemRequestDto, userId);
        return itemRequestClient.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findRequestsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get request userId={}", userId);
        return itemRequestClient.findRequestsByUserId(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable long requestId) {
        log.info("Get request {}, userId={}", requestId, userId);
        return itemRequestClient.getRequest(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(name = "size", defaultValue = "100") @Positive int size) {
        log.info("Get all requests");
        return itemRequestClient.findAllRequests(userId, from, size);
    }
}