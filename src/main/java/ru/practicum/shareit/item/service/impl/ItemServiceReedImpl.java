package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceRead;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(readOnly = true)
public class ItemServiceReedImpl implements ItemServiceRead {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemOutDto getItem(long userId, long itemId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Указанный userId не существует"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Указанный itemId не существует"));
        List<CommentDto> comments = commentRepository.findAllByItemId(item.getId()).stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
        if (Objects.equals(user.getId(), item.getOwner().getId())) {
            Booking lastBooking = bookingRepository.findFirstByItemIdAndStartBeforeAndStatus(item.getId(), LocalDateTime.now(), Status.APPROVED);
            Booking nextBooking = bookingRepository.findFirstByItemIdAndStartAfterAndStatus(item.getId(), LocalDateTime.now(), Status.APPROVED);
            return ItemMapper.toItemOutDto(item, lastBooking, nextBooking, comments);
        }
        return ItemMapper.toItemOutDto(item, comments);
    }

    @Override
    public List<ItemOutDto> findAllItems(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Указанный userId не существует"));
        return itemRepository.findItemsByOwnerIdOrderByIdAsc(user.getId()).stream()
                .map(Item -> getItem(user.getId(), Item.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(long userId, String text) {
        if (text.isBlank()) {
            return itemRepository.search(text).stream()
                    .filter(Item::getAvailable)
                    .filter(Item -> Item.getName().isBlank())
                    .filter(Item -> Item.getDescription().isBlank())
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
        return itemRepository.search(text).stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

}
