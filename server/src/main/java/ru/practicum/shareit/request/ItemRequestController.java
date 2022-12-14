package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.service.ItemRequestServiceCreate;
import ru.practicum.shareit.request.service.ItemRequestServiceRead;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class ItemRequestController {

    private final ItemRequestServiceCreate itemRequestServiceCreate;
    private final ItemRequestServiceRead itemRequestServiceRead;

    @PostMapping
    public ItemRequestDto addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Получен Post запрос к эндпоинту /requests");
        return itemRequestServiceCreate.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestOutDto> findRequestsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен Get запрос к эндпоинту /requests");
        return itemRequestServiceRead.findRequestsByUserId(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestOutDto getRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable long requestId) {
        log.info("Получен Get запрос к эндпоинту /requests/{}", requestId);
        return itemRequestServiceRead.getRequest(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestOutDto> findAllRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestParam int from,
                                                @RequestParam int size) {
        log.info("Получен Get запрос к эндпоинту /requests/all");
        return itemRequestServiceRead.findAllRequests(userId, PageRequest.of(from / size, size));
    }
}