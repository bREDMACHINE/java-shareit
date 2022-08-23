package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final List<User> users = new ArrayList<>();
    private long id = 1;

    @Override
    public User addUser(User createUser) {
        for (User user : users) {
            if (Objects.equals(user.getEmail(), createUser.getEmail())) {
                throw new ValidationException("указанная электронная почта уже используется");
            }
        }
        createUser.setId(id);
        id++;
        users.add(createUser);
        return createUser;
    }

    @Override
    public User updateUser(User user) {
        for (User updateUser : users) {
            if (Objects.equals(user.getEmail(), updateUser.getEmail()) && user.getId() != updateUser.getId()) {
                throw new ValidationException("указанная электронная почта уже используется");
            }
        }
        for (User updateUser : users) {
            if (user.getId() == updateUser.getId()) {
                updateUser.setName(user.getName());
                updateUser.setEmail(user.getEmail());
                return updateUser;
            }
        }
        return null;
    }

    @Override
    public Optional<User> getUser(long userId) {
        for (User user : users) {
            if (user.getId() == userId) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAllUsers() {
        return users;
    }

    @Override
    public void deleteUser(User user) {
        users.remove(user);
    }
}
