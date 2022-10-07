package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.service.BookingServiceCreate;
import ru.practicum.shareit.booking.service.BookingServiceRead;
import ru.practicum.shareit.booking.service.BookingServiceUpdate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @MockBean
    BookingServiceCreate bookingServiceCreate;
    @MockBean
    BookingServiceUpdate bookingServiceUpdate;
    @MockBean
    BookingServiceRead bookingServiceRead;
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;

    private final BookingDto bookingDto2 = new BookingDto(2L, LocalDateTime.of(2022, 9, 29, 19, 15,15),
            LocalDateTime.of(2022, 9, 29, 20, 15,15), 1L);
    private final BookingOutDto bookingOutDto2 = new BookingOutDto(2L, LocalDateTime.of(2022, 9, 29, 19, 15,15),
            LocalDateTime.of(2022, 9, 29, 20, 15,15), new BookingOutDto.ItemForBookingOutDto(1L, "nameUser1"), new BookingOutDto.UserForBookingOutDto(2L), Status.APPROVED);

    @Test
    void createBooking_ok() throws Exception {
        when(bookingServiceCreate.createBooking(2L, bookingDto2)).thenReturn(bookingDto2);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto2))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto2.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto2.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto2.getEnd().toString())))
                .andExpect(jsonPath("$.itemId", is(bookingDto2.getItemId()), Long.class));
        verify(bookingServiceCreate).createBooking(2L, bookingDto2);
    }

    @Test
    void updateStatus_ok() throws Exception {
        when(bookingServiceUpdate.updateStatus(1L, 2L, true)).thenReturn(bookingOutDto2);
        mvc.perform(patch("/bookings/2")
                        .content(mapper.writeValueAsString(bookingDto2))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOutDto2.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingOutDto2.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingOutDto2.getEnd().toString())))
                .andExpect(jsonPath("$.item.id", is(bookingOutDto2.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingOutDto2.getBooker().getId()), Long.class));
        verify(bookingServiceUpdate).updateStatus(1L, 2L, true);
    }

    @Test
    void getStatus_ok() throws Exception {
        when(bookingServiceRead.getStatus(2L, 2L)).thenReturn(bookingOutDto2);
        mvc.perform(get("/bookings/2")
                        .header("X-Sharer-User-Id", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOutDto2.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingOutDto2.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingOutDto2.getEnd().toString())))
                .andExpect(jsonPath("$.item.id", is(bookingOutDto2.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingOutDto2.getBooker().getId()), Long.class));
        verify(bookingServiceRead).getStatus(2L, 2L);
    }

    @Test
    void getStateBooker_ok() throws Exception {
        when(bookingServiceRead.getStateBooker(2L, State.FUTURE, PageRequest.of(0, 100))).thenReturn(List.of(bookingOutDto2));
        mvc.perform(get("/bookings")
                        .param("state", "FUTURE")
                        .param("from", "0")
                        .param("size", "100")
                        .header("X-Sharer-User-Id", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingOutDto2.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is(bookingOutDto2.getStart().toString())))
                .andExpect(jsonPath("$.[0].end", is(bookingOutDto2.getEnd().toString())))
                .andExpect(jsonPath("$.[0].item.id", is(bookingOutDto2.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingOutDto2.getBooker().getId()), Long.class));
        verify(bookingServiceRead).getStateBooker(2L, State.FUTURE, PageRequest.of(0, 100));
    }

    @Test
    void getStateOwner_ok() throws Exception {
        when(bookingServiceRead.getStateBooker(1L, State.FUTURE, PageRequest.of(0, 100))).thenReturn(List.of(bookingOutDto2));
        mvc.perform(get("/bookings")
                        .param("state", "FUTURE")
                        .param("from", "0")
                        .param("size", "100")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingOutDto2.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is(bookingOutDto2.getStart().toString())))
                .andExpect(jsonPath("$.[0].end", is(bookingOutDto2.getEnd().toString())))
                .andExpect(jsonPath("$.[0].item.id", is(bookingOutDto2.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingOutDto2.getBooker().getId()), Long.class));
        verify(bookingServiceRead).getStateBooker(1L, State.FUTURE, PageRequest.of(0, 100));
    }
}