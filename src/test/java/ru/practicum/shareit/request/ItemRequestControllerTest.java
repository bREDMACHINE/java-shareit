package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.service.ItemRequestServiceCreate;
import ru.practicum.shareit.request.service.ItemRequestServiceRead;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @MockBean
    ItemRequestServiceRead itemRequestServiceRead;
    @MockBean
    ItemRequestServiceCreate itemRequestServiceCreate;

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;

    private final ItemRequestDto itemRequestDto1 = new ItemRequestDto(1L,
            "DescriptionItemRequest1", 2L,
            LocalDateTime.of(2022, 9, 18, 18, 15,15));
    private final ItemRequestOutDto itemRequestOutDto1 = new ItemRequestOutDto(1L,
            "DescriptionItemRequest1", 2L,
            LocalDateTime.of(2022, 9, 18, 18, 15,15), List.of());


    @Test
    void addItem_ok() throws Exception {
        when(itemRequestServiceCreate.addItemRequest(2L, itemRequestDto1)).thenReturn(itemRequestDto1);

        mvc.perform(post("/requests")
                .content(mapper.writeValueAsString(itemRequestDto1))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", "2")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto1.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto1.getDescription())))
                .andExpect(jsonPath("$.requesterId", is(itemRequestDto1.getRequesterId()), Long.class))
                .andExpect(jsonPath("$.created", is(itemRequestDto1.getCreated().toString())));

        verify(itemRequestServiceCreate).addItemRequest(2L, itemRequestDto1);
    }

    @Test
    void findRequestsByUserId_ok() throws Exception {
        when(itemRequestServiceRead.findRequestsByUserId(2L)).thenReturn(List.of(itemRequestOutDto1));

        mvc.perform(get("/requests")
                .header("X-Sharer-User-Id", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestOutDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestOutDto1.getDescription())))
                .andExpect(jsonPath("$[0].requesterId", is(itemRequestOutDto1.getRequesterId()), Long.class))
                .andExpect(jsonPath("$[0].created", is(itemRequestOutDto1.getCreated().toString())))
                .andExpect(jsonPath("$[0].items", is(itemRequestOutDto1.getItems())));
        verify(itemRequestServiceRead).findRequestsByUserId(2L);
    }

    @Test
    void getRequest_ok() throws Exception {
        when(itemRequestServiceRead.getRequest(2L, 1L)).thenReturn(itemRequestOutDto1);

        mvc.perform(get("/requests/1")
                .header("X-Sharer-User-Id", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto1.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto1.getDescription())))
                .andExpect(jsonPath("$.requesterId", is(itemRequestDto1.getRequesterId()), Long.class))
                .andExpect(jsonPath("$.created", is(itemRequestDto1.getCreated().toString())))
                .andExpect(jsonPath("$.items", is(itemRequestOutDto1.getItems())));

        verify(itemRequestServiceRead).getRequest(2L, 1L);
    }

    @Test
    void findAllRequests_ok() throws Exception {
        when(itemRequestServiceRead.findAllRequests(2L, PageRequest.of(0,100))).thenReturn(List.of(itemRequestOutDto1));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "2")
                        .param("from", "0")
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestOutDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestOutDto1.getDescription())))
                .andExpect(jsonPath("$[0].requesterId", is(itemRequestOutDto1.getRequesterId()), Long.class))
                .andExpect(jsonPath("$[0].created", is(itemRequestOutDto1.getCreated().toString())))
                .andExpect(jsonPath("$[0].items", is(itemRequestOutDto1.getItems())));
        verify(itemRequestServiceRead).findAllRequests(2L, PageRequest.of(0,100));
    }
}