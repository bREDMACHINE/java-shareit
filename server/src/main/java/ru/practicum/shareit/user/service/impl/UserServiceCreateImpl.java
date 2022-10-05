package ru.practicum.shareit.user.service.impl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.service.UserServiceCreate;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class UserServiceCreateImpl implements UserServiceCreate {

    private final UserRepository userRepository;

    @Override
    public UserDto addUser(UserDto userDto) {
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }
}
