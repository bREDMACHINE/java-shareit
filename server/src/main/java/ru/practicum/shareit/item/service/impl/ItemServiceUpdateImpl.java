package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceUpdate;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Objects;


@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class ItemServiceUpdateImpl implements ItemServiceUpdate {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        Item updateItem = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Указанный itemId не существует"));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Указанный userId не существует"));
        if (Objects.equals(updateItem.getOwner().getId(), user.getId())) {
            if (itemDto.getName() != null) {
                updateItem.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                updateItem.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                updateItem.setAvailable(itemDto.getAvailable());
            }
            return ItemMapper.toItemDto(itemRepository.save(updateItem));
        } else {
            throw new NotFoundException("userId не является владельцем, недоступно изменение параметров");
        }
    }
}
