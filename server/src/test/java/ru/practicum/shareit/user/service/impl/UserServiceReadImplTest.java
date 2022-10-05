package ru.practicum.shareit.user.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.service.UserServiceRead;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceReadImplTest {

    private UserServiceRead userServiceRead;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userServiceRead = new UserServiceReadImpl(userRepository);
    }

    @Test
    void getUser_ok() {
        UserDto userDto2 = new UserDto(2L, "nameUser2", "email@User2");
        when(userRepository.findById(2L)).thenReturn(Optional.of(UserMapper.toUser(userDto2)));
        UserDto userDto2Check = userServiceRead.getUser(2L);
        assertThat(userDto2Check.getId(), equalTo(userDto2.getId()));
        assertThat(userDto2Check.getName(), equalTo(userDto2.getName()));
        assertThat(userDto2Check.getEmail(), equalTo(userDto2.getEmail()));
        verify(userRepository).findById(2L);
    }

    @Test
    void findAllUsers_ok() {
        User user1 = new User(1L, "nameUser1", "email@User1");
        User user2 = new User(2L, "nameUser2", "email@User2");
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        List<UserDto> userList = userServiceRead.findAllUsers();
        assertNotNull(userList);
        assertEquals(2, userList.size());
        verify(userRepository).findAll();
    }
}