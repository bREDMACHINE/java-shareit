package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto addUser(UserDto userDto) {
        return UserMapper.toUserDto(userRepository.addUser(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        UserDto updateUser = getUser(userId);
        if (userDto.getName() != null) {
            updateUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            updateUser.setEmail(userDto.getEmail());
        }
        return UserMapper.toUserDto(userRepository.updateUser(UserMapper.toUser(updateUser)));
    }

    @Override
    public UserDto getUser(long userId) {
        return UserMapper.toUserDto(userRepository.getUser(userId).orElseThrow(() -> new NotFoundException("указанный userId не существует")));
    }

    @Override
    public List<UserDto> findAllUsers() {
        return userRepository.findAllUsers().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteUser(UserMapper.toUser(getUser(userId)));
    }
}
