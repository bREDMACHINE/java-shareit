package ru.practicum.shareit.user.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.service.UserServiceCreate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceCreateImplTest {

    private UserServiceCreate userServiceCreate;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userServiceCreate = new UserServiceCreateImpl(userRepository);
    }

    @Test
    void addUser_ok() {
        UserDto userDto1 = new UserDto(1L, "nameUser1", "email@User1");
        when(userRepository.save(UserMapper.toUser(userDto1))).thenReturn(UserMapper.toUser(userDto1));
        UserDto userDto1Check = userServiceCreate.addUser(userDto1);
        assertThat(userDto1Check.getId(), equalTo(userDto1.getId()));
        assertThat(userDto1Check.getName(), equalTo(userDto1.getName()));
        assertThat(userDto1Check.getEmail(), equalTo(userDto1.getEmail()));
        verify(userRepository).save(UserMapper.toUser(userDto1));
    }
}