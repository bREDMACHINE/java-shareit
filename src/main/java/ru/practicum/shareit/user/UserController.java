package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserServiceCreate;
import ru.practicum.shareit.user.service.UserServiceDelete;
import ru.practicum.shareit.user.service.UserServiceRead;
import ru.practicum.shareit.user.service.UserServiceUpdate;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class UserController {

    private final UserServiceCreate userServiceCreate;
    private final UserServiceUpdate userServiceUpdate;
    private final UserServiceRead userServiceRead;
    private final UserServiceDelete userServiceDelete;

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        log.info("Получен Post запрос к эндпоинту /users");
        return userServiceCreate.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable long userId, @RequestBody UserDto userDto) {
        log.info("Получен Patch запрос к эндпоинту /users/{}", userId);
        return userServiceUpdate.updateUser(userId, userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        log.info("Получен Get запрос к эндпоинту /users/{}", userId);
        return userServiceRead.getUser(userId);
    }

    @GetMapping
    public List<UserDto> findAllUsers() {
        log.info("Получен Get запрос к эндпоинту /users");
        return userServiceRead.findAllUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("Получен Delete запрос к эндпоинту /users");
        userServiceDelete.deleteUser(userId);
    }
}
