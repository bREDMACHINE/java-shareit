package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
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
class BookingRepositoryTest {

    @Autowired
    BookingRepository bookingRepository;
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
    Booking booking1;
    Booking booking2;

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
        booking1 = bookingRepository.save(new Booking(1L, LocalDateTime.of(2022, 9, 18, 19, 15,15),
                LocalDateTime.of(2022, 9, 18, 20, 15,15), item1, user2, Status.APPROVED));
        booking2 = bookingRepository.save(new Booking(2L, LocalDateTime.of(2022, 9, 29, 19, 15,15),
                LocalDateTime.of(2022, 9, 29, 20, 15,15), item1, user2, Status.APPROVED));
    }

    @AfterEach
    void afterEach() {
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    void findByItemOwnerIdOrderByStartDesc_ok() {
        List<Booking> bookingsCheck = bookingRepository.findByItemOwnerIdOrderByStartDesc(booking2.getItem().getOwner().getId(), PageRequest.of(0, 100));
        assertNotNull(bookingsCheck);
        assertThat(2, equalTo(bookingsCheck.size()));
        assertThat(bookingsCheck.get(0).getId(), equalTo(booking2.getId()));
        assertThat(bookingsCheck.get(0).getItem().getId(), equalTo(booking2.getItem().getId()));
        assertThat(bookingsCheck.get(0).getBooker().getId(), equalTo(booking2.getBooker().getId()));
        assertThat(bookingsCheck.get(0).getStatus(), equalTo(booking2.getStatus()));
    }

    @Test
    void findByBookerIdOrderByStartDesc_ok() {
        List<Booking> bookingsCheck = bookingRepository.findByBookerIdOrderByStartDesc(booking2.getBooker().getId(), PageRequest.of(0, 100));
        assertNotNull(bookingsCheck);
        assertThat(2, equalTo(bookingsCheck.size()));
        assertThat(bookingsCheck.get(0).getId(), equalTo(booking2.getId()));
        assertThat(bookingsCheck.get(0).getItem().getId(), equalTo(booking2.getItem().getId()));
        assertThat(bookingsCheck.get(0).getBooker().getId(), equalTo(booking2.getBooker().getId()));
        assertThat(bookingsCheck.get(0).getStatus(), equalTo(booking2.getStatus()));
    }

    @Test
    void findFirstByItemIdAndStartBeforeAndStatus_ok() {
        Booking bookingCheck = bookingRepository.findFirstByItemIdAndStartBeforeAndStatus(booking1.getItem().getId(), LocalDateTime.now(), Status.APPROVED);
        assertNotNull(bookingCheck);
        assertThat(bookingCheck.getId(), equalTo(booking1.getId()));
        assertThat(bookingCheck.getItem().getId(), equalTo(booking1.getItem().getId()));
        assertThat(bookingCheck.getBooker().getId(), equalTo(booking1.getBooker().getId()));
        assertThat(bookingCheck.getStatus(), equalTo(booking1.getStatus()));
    }

    @Test
    void findFirstByItemIdAndStartAfterAndStatus_ok() {
        Booking bookingCheck = bookingRepository.findFirstByItemIdAndStartAfterAndStatus(booking1.getItem().getId(), LocalDateTime.now(), Status.APPROVED);
        assertNotNull(bookingCheck);
        assertThat(bookingCheck.getId(), equalTo(booking2.getId()));
        assertThat(bookingCheck.getItem().getId(), equalTo(booking2.getItem().getId()));
        assertThat(bookingCheck.getBooker().getId(), equalTo(booking2.getBooker().getId()));
        assertThat(bookingCheck.getStatus(), equalTo(booking2.getStatus()));
    }

    @Test
    void findFirstByBookerIdAndItemIdAndEndBefore_ok() {
        Booking bookingCheck = bookingRepository.findFirstByBookerIdAndItemIdAndEndBefore(booking1.getBooker().getId(), booking1.getItem().getId(), LocalDateTime.now()).get();
        assertNotNull(bookingCheck);
        assertThat(bookingCheck.getId(), equalTo(booking1.getId()));
        assertThat(bookingCheck.getItem().getId(), equalTo(booking1.getItem().getId()));
        assertThat(bookingCheck.getBooker().getId(), equalTo(booking1.getBooker().getId()));
        assertThat(bookingCheck.getStatus(), equalTo(booking1.getStatus()));
    }
}