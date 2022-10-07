package ru.practicum.shareit.request.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.service.ItemRequestServiceRead;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(readOnly = true)
public class ItemRequestServiceReadImpl implements ItemRequestServiceRead {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public List<ItemRequestOutDto> findRequestsByUserId(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Указанный userId не существует"));
        return itemRequestRepository.findByRequesterId(user.getId()).stream()
                .map(ItemRequest -> getRequest(userId, ItemRequest.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestOutDto getRequest(long userId, long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Указанный userId не существует"));
        List<ItemDto> items = itemRepository.findByRequestId(requestId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        return ItemRequestMapper.toItemRequestOutDto(itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Указанный requestId не существует")), items);
    }

    @Override
    public List<ItemRequestOutDto> findAllRequests(long userId, Pageable pageable) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Указанный userId не существует"));
        return itemRequestRepository.findAll(pageable).stream()
                .filter(ItemRequest -> !Objects.equals(ItemRequest.getRequester().getId(), user.getId()))
                .map(ItemRequest -> getRequest(userId, ItemRequest.getId()))
                .collect(Collectors.toList());
    }
}
