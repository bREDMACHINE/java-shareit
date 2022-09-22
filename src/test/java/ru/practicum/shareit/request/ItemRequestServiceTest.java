package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    private ItemRequestService itemRequestService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);
    }

    private final ItemRequestDto itemRequestDto1 = new ItemRequestDto(1L,
            "DescriptionItemRequest1", 2L,
            LocalDateTime.of(2022, 9, 18, 18, 15,15));
    private final User user1 = new User(1L, "nameUser1", "email@User1");
    private final User user2 = new User(2L, "nameUser2", "email@User2");

    @Test
    void addItemRequest_ok() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(itemRequestRepository.save(ItemRequestMapper.toItemRequest(itemRequestDto1, user2)))
                .thenReturn(ItemRequestMapper.toItemRequest(itemRequestDto1, user2));
        ItemRequestDto itemRequestDtoCheck = itemRequestService.addItemRequest(2,itemRequestDto1);
        assertThat(itemRequestDtoCheck.getId(), equalTo(itemRequestDto1.getId()));
        assertThat(itemRequestDtoCheck.getRequesterId(), equalTo(itemRequestDto1.getRequesterId()));
        assertThat(itemRequestDtoCheck.getDescription(), equalTo(itemRequestDto1.getDescription()));
        assertThat(itemRequestDtoCheck.getCreated(), equalTo(itemRequestDto1.getCreated()));
        verify(userRepository).findById(2L);
        verify(itemRequestRepository).save(ItemRequestMapper.toItemRequest(itemRequestDto1, user2));
    }

    @Test
    void findRequestsByUserId_ok() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(itemRequestRepository.findByRequesterId(2L))
                .thenReturn(List.of(ItemRequestMapper.toItemRequest(itemRequestDto1, user2)));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(ItemRequestMapper.toItemRequest(itemRequestDto1, user2)));
        List<ItemRequestOutDto> itemRequestList = itemRequestService.findRequestsByUserId(2L);
        assertNotNull(itemRequestList);
        assertEquals(1, itemRequestList.size());
        verify(userRepository, times(2)).findById(2L);
        verify(itemRequestRepository).findByRequesterId(2L);
        verify(itemRequestRepository).findById(1L);
    }

    @Test
    void getRequest_ok() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(ItemRequestMapper.toItemRequest(itemRequestDto1, user2)));
        ItemRequestOutDto itemRequestCheck = itemRequestService.getRequest(1L, 1L);
        assertThat(itemRequestCheck.getId(), equalTo(itemRequestDto1.getId()));
        assertThat(itemRequestCheck.getRequesterId(), equalTo(itemRequestDto1.getRequesterId()));
        assertThat(itemRequestCheck.getDescription(), equalTo(itemRequestDto1.getDescription()));
        assertThat(itemRequestCheck.getCreated(), equalTo(itemRequestDto1.getCreated()));
        verify(userRepository).findById(1L);
        verify(itemRequestRepository).findById(1L);
    }

    @Test
    void findAllRequests_ok() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        Pageable pageable = PageRequest.of(0, 100);
        when(itemRequestRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(ItemRequestMapper.toItemRequest(itemRequestDto1, user2))));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(ItemRequestMapper.toItemRequest(itemRequestDto1, user2)));
        List<ItemRequestOutDto> itemRequestList = itemRequestService.findAllRequests(1L, pageable);
        assertNotNull(itemRequestList);
        assertEquals(1, itemRequestList.size());
        verify(userRepository, times(2)).findById(1L);
        verify(itemRequestRepository).findAll(pageable);
        verify(itemRequestRepository).findById(1L);
    }
}