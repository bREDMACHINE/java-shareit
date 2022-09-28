package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.UserDto;

public interface UserServiceUpdate {

    UserDto updateUser(long userId, UserDto userDto);
}
