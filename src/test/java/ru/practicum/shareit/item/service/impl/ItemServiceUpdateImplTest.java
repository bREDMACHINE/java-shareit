package ru.practicum.shareit.item.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceUpdate;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceUpdateImplTest {

    private ItemServiceUpdate itemServiceUpdate;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        itemServiceUpdate = new ItemServiceUpdateImpl(itemRepository, userRepository);
    }

    private final User user1 = new User(1L, "nameUser1", "email@User1");
    private final User user2 = new User(2L, "nameUser2", "email@User2");
    private final ItemRequest itemRequest1 = new ItemRequest(1L,
            "DescriptionItemRequest1", user2,
            LocalDateTime.of(2022, 9, 18, 18, 15,15));
    private final Item item1 = new Item(1L, "nameItem1", "descriptionItem1", true, user1, itemRequest1);
    private final ItemDto itemDto1 = new ItemDto(1L, "nameItem1", "descriptionItem1", true, 1L);
    private final ItemDto itemDto4 = new ItemDto(4L, null, null, null, 1L);
    private final ItemDto updateItemDto1 = new ItemDto(1L, "updateNameItem1", "updateDescriptionItem1", true, 1L);
    private final Item updateItem1 = new Item(1L, "updateNameItem1", "updateDescriptionItem1", true, user1, itemRequest1);

    @Test
    void updateItem_ok() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(itemRepository.save(updateItem1)).thenReturn(updateItem1);
        ItemDto itemDtoCheck = itemServiceUpdate.updateItem(1L, 1L, updateItemDto1);
        assertThat(itemDtoCheck.getId(), equalTo(updateItemDto1.getId()));
        assertThat(itemDtoCheck.getName(), equalTo(updateItemDto1.getName()));
        assertThat(itemDtoCheck.getDescription(), equalTo(updateItemDto1.getDescription()));
        assertThat(itemDtoCheck.getAvailable(), equalTo(updateItemDto1.getAvailable()));
        assertThat(itemDtoCheck.getRequestId(), equalTo(updateItemDto1.getRequestId()));
        verify(userRepository).findById(1L);
        verify(itemRepository).findById(1L);
        verify(itemRepository).save(updateItem1);
    }

    @Test
    void updateItemWithNull_ok() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(itemRepository.save(item1)).thenReturn(item1);
        ItemDto itemDtoCheck = itemServiceUpdate.updateItem(1L, 1L, itemDto4);
        assertThat(itemDtoCheck.getId(), equalTo(item1.getId()));
        assertThat(itemDtoCheck.getName(), equalTo(item1.getName()));
        assertThat(itemDtoCheck.getDescription(), equalTo(item1.getDescription()));
        assertThat(itemDtoCheck.getAvailable(), equalTo(item1.getAvailable()));
        assertThat(itemDtoCheck.getRequestId(), equalTo(itemDto4.getRequestId()));
        verify(userRepository).findById(1L);
        verify(itemRepository).findById(1L);
        verify(itemRepository).save(item1);
    }

    @Test
    void updateItemNotFoundOwner_error() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemServiceUpdate.updateItem(2L, 1L, itemDto1));
        Assertions.assertEquals("userId не является владельцем, недоступно изменение параметров", exception.getMessage());
        verify(userRepository).findById(2L);
        verify(itemRepository).findById(1L);
    }
}