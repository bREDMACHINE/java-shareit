package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User addUser(User user);

    User updateUser(User newUser);

    Optional<User> getUser(long userId);

    List<User> findAllUsers();

    void deleteUser(User user);
}
