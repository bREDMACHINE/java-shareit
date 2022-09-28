package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;

import java.util.List;

public interface ItemRequestServiceRead {

    List<ItemRequestOutDto> findRequestsByUserId(long userId);

    ItemRequestOutDto getRequest(long userId, long requestId);

    List<ItemRequestOutDto> findAllRequests(long userId, Pageable pageable);
}
