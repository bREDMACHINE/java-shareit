package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;

    User user1;
    User user2;
    ItemRequest itemRequest1;
    ItemRequest itemRequest2;
    Item item1;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(new User(1L, "nameUser1", "email@User1"));
        user2 = userRepository.save(new User(2L, "nameUser2", "email@User2"));
        itemRequest1 = itemRequestRepository.save(new ItemRequest(1L,
                "DescriptionItemRequest1", user2,
                LocalDateTime.of(2022, 9, 18, 18, 15,15)));
        itemRequest2 = itemRequestRepository.save(new ItemRequest(2L,
                "DescriptionItemRequest2", user2,
                LocalDateTime.of(2022, 9, 18, 18, 15,15)));
        item1 = itemRepository.save(new Item(1L, "nameItem1", "descriptionItem1", true, user1, itemRequest1));
    }

    @AfterEach
    void afterEach() {
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    void search_ok() {
        List<Item> itemsCheck = itemRepository.search("description");
        assertNotNull(itemsCheck);
        assertThat(1, equalTo(itemsCheck.size()));
        assertThat(itemsCheck.get(0).getId(), equalTo(item1.getId()));
        assertThat(itemsCheck.get(0).getDescription(), equalTo(item1.getDescription()));
        assertThat(itemsCheck.get(0).getOwner().getId(), equalTo(item1.getOwner().getId()));
        assertThat(itemsCheck.get(0).getName(), equalTo(item1.getName()));
        assertThat(itemsCheck.get(0).getAvailable(), equalTo(item1.getAvailable()));
    }

    @Test
    void findItemsByOwnerIdOrderByIdAsc_ok() {
        List<Item> itemsCheck = itemRepository.findItemsByOwnerIdOrderByIdAsc(user1.getId());
        assertNotNull(itemsCheck);
        assertThat(1, equalTo(itemsCheck.size()));
        assertThat(itemsCheck.get(0).getId(), equalTo(item1.getId()));
        assertThat(itemsCheck.get(0).getDescription(), equalTo(item1.getDescription()));
        assertThat(itemsCheck.get(0).getOwner().getId(), equalTo(item1.getOwner().getId()));
        assertThat(itemsCheck.get(0).getName(), equalTo(item1.getName()));
        assertThat(itemsCheck.get(0).getAvailable(), equalTo(item1.getAvailable()));
    }

    @Test
    void findByRequestId_ok() {
        List<Item> itemsCheck = itemRepository.findByRequestId(user1.getId());
        assertNotNull(itemsCheck);
        assertThat(1, equalTo(itemsCheck.size()));
        assertThat(itemsCheck.get(0).getId(), equalTo(item1.getId()));
        assertThat(itemsCheck.get(0).getDescription(), equalTo(item1.getDescription()));
        assertThat(itemsCheck.get(0).getOwner().getId(), equalTo(item1.getOwner().getId()));
        assertThat(itemsCheck.get(0).getName(), equalTo(item1.getName()));
        assertThat(itemsCheck.get(0).getAvailable(), equalTo(item1.getAvailable()));
    }
}