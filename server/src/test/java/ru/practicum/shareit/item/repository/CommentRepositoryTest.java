package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
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
class CommentRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    CommentRepository commentRepository;

    User user1;
    User user2;
    ItemRequest itemRequest1;
    ItemRequest itemRequest2;
    Item item1;
    Comment comment1;
    Comment comment2;

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
        comment1 = commentRepository.save(new Comment(1L, "Comment1", item1, user2, LocalDateTime.of(2022, 11, 18, 18, 15,15)));
        comment2 = commentRepository.save(new Comment(2L, "Comment2", item1, user2, LocalDateTime.of(2022, 11, 20, 18, 15,15)));
    }

    @AfterEach
    void afterEach() {
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
        commentRepository.deleteAll();
    }

    @Test
    void findAllByItemId() {
        List<Comment> commentsCheck = commentRepository.findAllByItemId(item1.getId());
        assertNotNull(commentsCheck);
        assertThat(2, equalTo(commentsCheck.size()));
        assertThat(commentsCheck.get(0).getId(), equalTo(comment1.getId()));
        assertThat(commentsCheck.get(0).getItem().getId(), equalTo(comment1.getItem().getId()));
        assertThat(commentsCheck.get(0).getText(), equalTo(comment1.getText()));
        assertThat(commentsCheck.get(0).getCreated(), equalTo(comment1.getCreated()));
        assertThat(commentsCheck.get(0).getAuthor().getId(), equalTo(comment1.getAuthor().getId()));
    }
}