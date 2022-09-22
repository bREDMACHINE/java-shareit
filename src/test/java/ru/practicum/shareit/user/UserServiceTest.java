package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void addUser_ok() {
        UserDto userDto1 = new UserDto(1L, "nameUser1", "email@User1");
        when(userRepository.save(UserMapper.toUser(userDto1))).thenReturn(UserMapper.toUser(userDto1));
        UserDto userDto1Check = userService.addUser(userDto1);
        assertThat(userDto1Check.getId(), equalTo(userDto1.getId()));
        assertThat(userDto1Check.getName(), equalTo(userDto1.getName()));
        assertThat(userDto1Check.getEmail(), equalTo(userDto1.getEmail()));
        verify(userRepository).save(UserMapper.toUser(userDto1));
    }

    @Test
    void updateUser_ok() {
        UserDto userDto1 = new UserDto(1L, "nameUser1", "email@User1");
        UserDto updateUserDto1 = new UserDto(1L, "updateNameUser1", "updateEmail@User1");
        when(userRepository.findById(1L)).thenReturn(Optional.of(UserMapper.toUser(userDto1)));
        when(userRepository.save(UserMapper.toUser(updateUserDto1))).thenReturn(UserMapper.toUser(updateUserDto1));
        UserDto userDto1Check = userService.updateUser(1L, updateUserDto1);
        assertThat(userDto1Check.getId(), equalTo(updateUserDto1.getId()));
        assertThat(userDto1Check.getName(), equalTo(updateUserDto1.getName()));
        assertThat(userDto1Check.getEmail(), equalTo(updateUserDto1.getEmail()));
        verify(userRepository).findById(1L);
        verify(userRepository).save(UserMapper.toUser(updateUserDto1));
    }

    @Test
    void getUser_ok() {
        UserDto userDto2 = new UserDto(2L, "nameUser2", "email@User2");
        when(userRepository.findById(2L)).thenReturn(Optional.of(UserMapper.toUser(userDto2)));
        UserDto userDto2Check = userService.getUser(2L);
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
        List<UserDto> userList = userService.findAllUsers();
        assertNotNull(userList);
        assertEquals(2, userList.size());
        verify(userRepository).findAll();
    }

    @Test
    void deleteUser_ok() {
        User user2 = new User(2L, "nameUser2", "email@User2");
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        userService.deleteUser(2L);
        verify(userRepository).delete(user2);
    }
}