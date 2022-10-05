package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @RequestBody UserDto userDto) {
        log.info("Creating user {}",  userDto);
        return userClient.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable long userId, @RequestBody UserDto userDto) {
        log.info("Patch user {}", userId);
        return userClient.updateUser(userId, userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable long userId) {
        log.info("Get user {}", userId);
        return userClient.getUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllUsers() {
        log.info("Get all users");
        return userClient.findAllUsers();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable long userId) {
        log.info("Delete user {}", userId);
        return userClient.deleteUser(userId);
    }
}
