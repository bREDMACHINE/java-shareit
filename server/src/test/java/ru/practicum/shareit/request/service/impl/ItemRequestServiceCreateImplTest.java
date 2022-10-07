package ru.practicum.shareit.request.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestServiceCreate;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceCreateImplTest {

    private ItemRequestServiceCreate itemRequestServiceCreate;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        itemRequestServiceCreate = new ItemRequestServiceCreateImpl(itemRequestRepository, userRepository);
    }

    private final ItemRequestDto itemRequestDto1 = new ItemRequestDto(1L,
            "DescriptionItemRequest1", 2L,
            LocalDateTime.of(2022, 9, 18, 18, 15,15));
    private final User user2 = new User(2L, "nameUser2", "email@User2");

    @Test
    void addItemRequest_ok() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(itemRequestRepository.save(ItemRequestMapper.toItemRequest(itemRequestDto1, user2)))
                .thenReturn(ItemRequestMapper.toItemRequest(itemRequestDto1, user2));
        ItemRequestDto itemRequestDtoCheck = itemRequestServiceCreate.addItemRequest(2,itemRequestDto1);
        assertThat(itemRequestDtoCheck.getId(), equalTo(itemRequestDto1.getId()));
        assertThat(itemRequestDtoCheck.getRequesterId(), equalTo(itemRequestDto1.getRequesterId()));
        assertThat(itemRequestDtoCheck.getDescription(), equalTo(itemRequestDto1.getDescription()));
        assertThat(itemRequestDtoCheck.getCreated(), equalTo(itemRequestDto1.getCreated()));
        verify(userRepository).findById(2L);
        verify(itemRequestRepository).save(ItemRequestMapper.toItemRequest(itemRequestDto1, user2));
    }
}