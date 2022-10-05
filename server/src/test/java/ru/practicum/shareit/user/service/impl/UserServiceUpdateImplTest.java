package ru.practicum.shareit.user.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.service.UserServiceUpdate;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceUpdateImplTest {

    private UserServiceUpdate userServiceUpdate;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userServiceUpdate = new UserServiceUpdateImpl(userRepository);
    }

    @Test
    void updateUser_ok() {
        UserDto userDto1 = new UserDto(1L, "nameUser1", "email@User1");
        UserDto updateUserDto1 = new UserDto(1L, "updateNameUser1", "updateEmail@User1");
        when(userRepository.findById(1L)).thenReturn(Optional.of(UserMapper.toUser(userDto1)));
        when(userRepository.save(UserMapper.toUser(updateUserDto1))).thenReturn(UserMapper.toUser(updateUserDto1));
        UserDto userDto1Check = userServiceUpdate.updateUser(1L, updateUserDto1);
        assertThat(userDto1Check.getId(), equalTo(updateUserDto1.getId()));
        assertThat(userDto1Check.getName(), equalTo(updateUserDto1.getName()));
        assertThat(userDto1Check.getEmail(), equalTo(updateUserDto1.getEmail()));
        verify(userRepository).findById(1L);
        verify(userRepository).save(UserMapper.toUser(updateUserDto1));
    }
}