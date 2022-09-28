package ru.practicum.shareit.user.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.service.UserServiceDelete;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceDeleteImplTest {

    private UserServiceDelete userServiceDelete;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userServiceDelete = new UserServiceDeleteImpl(userRepository);
    }

    @Test
    void deleteUser_ok() {
        User user2 = new User(2L, "nameUser2", "email@User2");
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        userServiceDelete.deleteUser(2L);
        verify(userRepository).delete(user2);
    }
}