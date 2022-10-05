package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    UserRepository userRepository;
    User user1;
    User user2;
    ItemRequest itemRequest1;
    ItemRequest itemRequest2;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(new User(1L, "nameUser1", "email@User1"));
        user2 = userRepository.save(new User(2L, "nameUser2", "email@User2"));
        itemRequest1 = itemRequestRepository.save(new ItemRequest(1L,
                "DescriptionItemRequest1", user2,
                LocalDateTime.of(2022, 9, 18, 18, 15,15)));
        itemRequest2 = itemRequestRepository.save(new ItemRequest(2L,
                "DescriptionItemRequest1", user1,
                LocalDateTime.of(2022, 9, 18, 18, 15,15)));
    }

    @AfterEach
    void afterEach() {
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findByRequesterId_ok() {
        List<ItemRequest> itemRequestCheck = itemRequestRepository.findByRequesterId(itemRequest1.getRequester().getId());
        assertNotNull(itemRequestCheck);
        assertThat(1, equalTo(itemRequestCheck.size()));
        assertThat(itemRequestCheck.get(0).getId(), equalTo(itemRequest1.getId()));
        assertThat(itemRequestCheck.get(0).getRequester().getId(), equalTo(itemRequest1.getRequester().getId()));
        assertThat(itemRequestCheck.get(0).getDescription(), equalTo(itemRequest1.getDescription()));
        assertThat(itemRequestCheck.get(0).getCreated(), equalTo(itemRequest1.getCreated()));

    }
}