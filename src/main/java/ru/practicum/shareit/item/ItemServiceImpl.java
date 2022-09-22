package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Указанный userId не существует"));
        Item item;
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Указанный requestId не существует"));
            item = ItemMapper.toItem(itemDto, user, itemRequest);
        } else {
            item = ItemMapper.toItem(itemDto, user);
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
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
        return itemRepository.findItemsByOwnerIdOrderByIdAsc(user.getId()).stream().map(Item -> getItem(user.getId(), Item.getId())).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(long userId, String text) {
        if (text.isBlank()) {
            return itemRepository.search(text).stream().filter(Item::getAvailable).filter(Item -> Item.getName().isBlank()).filter(Item -> Item.getDescription().isBlank()).map(ItemMapper::toItemDto).collect(Collectors.toList());
        }
        return itemRepository.search(text).stream().filter(Item::getAvailable).map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Указанный userId не существует"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Указанный itemId не существует"));
        LocalDateTime created = LocalDateTime.now();
        Comment comment = CommentMapper.toComment(commentDto, item, user, created);
        if (bookingRepository.findFirstByBookerIdAndItemIdAndEndBefore(user.getId(), item.getId(), created).isPresent()) {
            return CommentMapper.toCommentDto(commentRepository.save(comment));
        } else {
            throw new BadRequestException("Добавление комментариев недоступно");
        }
    }
}
