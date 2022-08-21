package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        itemDto.setOwner(UserMapper.toUser(userService.getUser(userId)));
        return ItemMapper.toItemDto(itemRepository.addItem(ItemMapper.toItem(itemDto)));
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        if (itemRepository.getItem(itemId).orElseThrow(() -> new NotFoundException("указанный itemId не существует")).getOwner().getId() == userService.getUser(userId).getId()) {
            ItemDto updateItemDto = getItem(userId, itemId);
            if (itemDto.getName() != null) {
                updateItemDto.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                updateItemDto.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                updateItemDto.setAvailable(itemDto.getAvailable());
            }
            return ItemMapper.toItemDto(itemRepository.updateItem(ItemMapper.toItem(updateItemDto)));
        } else {
            throw new NotFoundException("userId не является владельцем, недоступно изменение параметров");
        }
    }

    @Override
    public ItemDto getItem(long userId, long itemId) {
        return ItemMapper.toItemDto(itemRepository.getItem(itemId).orElseThrow(() -> new NotFoundException("указанный itemId не существует")));
    }

    @Override
    public List<ItemDto> findAllItems(long userId) {
        return itemRepository.findAllItems(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(long userId, String text) {
        return itemRepository.searchItems(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}
