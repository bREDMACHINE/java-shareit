package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.item.service.ItemServiceCreate;
import ru.practicum.shareit.item.service.ItemServiceRead;
import ru.practicum.shareit.item.service.ItemServiceUpdate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @MockBean
    ItemServiceCreate itemServiceCreate;
    @MockBean
    ItemServiceUpdate itemServiceUpdate;
    @MockBean
    ItemServiceRead itemServiceRead;
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;

    private final ItemDto itemDto1 = new ItemDto(1L, "nameItem1", "descriptionItem1", true, 1L);
    private final ItemDto updateItemDto1 = new ItemDto(1L, "updateNameItem1", "updateDescriptionItem1", true, 1L);
    private final CommentDto commentDto1 = new CommentDto(1L, "comment1", "nameUser2", LocalDateTime.of(2022, 9, 19, 18, 15,15));
    private final CommentDto commentDto2 = new CommentDto(2L, "comment2", "nameUser2", LocalDateTime.of(2022, 10, 18, 18, 15,15));
    private final ItemOutDto itemOutDto = new ItemOutDto(1L, "nameItem1", "descriptionItem1", true,
            new ItemOutDto.BookingForItemOutDto(1L, 2L), new ItemOutDto.BookingForItemOutDto(2L, 2L),
            List.of(commentDto1, commentDto2));

    @Test
    void addItem_ok() throws Exception {
        when(itemServiceCreate.addItem(1L, itemDto1)).thenReturn(itemDto1);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto1.getName())))
                .andExpect(jsonPath("$.description", is(itemDto1.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto1.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto1.getRequestId()), Long.class));
        verify(itemServiceCreate).addItem(1L, itemDto1);
    }

    @Test
    void updateItem_ok() throws Exception {
        when(itemServiceUpdate.updateItem(1L, 1L, updateItemDto1)).thenReturn(updateItemDto1);
        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(updateItemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updateItemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updateItemDto1.getName())))
                .andExpect(jsonPath("$.description", is(updateItemDto1.getDescription())))
                .andExpect(jsonPath("$.available", is(updateItemDto1.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(updateItemDto1.getRequestId()), Long.class));
        verify(itemServiceUpdate).updateItem(1L, 1L, updateItemDto1);
    }

    @Test
    void getItem_ok() throws Exception {
        when(itemServiceRead.getItem(1L, 1L)).thenReturn(itemOutDto);
        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemOutDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemOutDto.getName())))
                .andExpect(jsonPath("$.description", is(itemOutDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemOutDto.getAvailable())));
        verify(itemServiceRead).getItem(1L, 1L);
    }

    @Test
    void findAllItems_ok() throws Exception {
        when(itemServiceRead.findAllItems(1L)).thenReturn(List.of(itemOutDto));
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemOutDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemOutDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemOutDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemOutDto.getAvailable())));
        verify(itemServiceRead).findAllItems(1L);
    }

    @Test
    void searchItems_ok() throws Exception {
        when(itemServiceRead.searchItems(1L, "text")).thenReturn(List.of(itemDto1));
        mvc.perform(get("/items/search")
                        .param("text", "text")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemDto1.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDto1.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDto1.getAvailable())))
                .andExpect(jsonPath("$.[0].requestId", is(itemDto1.getRequestId()), Long.class));
        verify(itemServiceRead).searchItems(1L, "text");
    }

    @Test
    void addComment_ok() throws Exception {
        when(itemServiceCreate.addComment(2L,1L, commentDto1)).thenReturn(commentDto1);
        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto1.getId()), Long.class))
                .andExpect(jsonPath("$.authorName", is(commentDto1.getAuthorName())))
                .andExpect(jsonPath("$.text", is(commentDto1.getText())))
                .andExpect(jsonPath("$.created", is(commentDto1.getCreated().toString())));
        verify(itemServiceCreate).addComment(2L, 1L, commentDto1);
    }
}