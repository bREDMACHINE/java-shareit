package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {

    UserDto addUser(UserDto userDto);

    UserDto updateUser(long userId, UserDto userDto);

    UserDto getUser(long userId);

    List<UserDto> findAllUsers();

    void deleteUser(long userId);
}
