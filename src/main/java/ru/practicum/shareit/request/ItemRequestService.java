package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addItemRequest(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestOutDto> findRequestsByUserId(long userId);

    ItemRequestOutDto getRequest(long userId, long requestId);

    List<ItemRequestOutDto> findAllRequests(long userId, Pageable pageable);
}
