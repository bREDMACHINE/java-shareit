package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.UserDto;

import java.util.List;

public interface UserServiceRead {

    UserDto getUser(long userId);

    List<UserDto> findAllUsers();
}
