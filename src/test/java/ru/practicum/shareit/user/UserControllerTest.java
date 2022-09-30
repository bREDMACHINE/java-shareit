package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.service.UserServiceCreate;
import ru.practicum.shareit.user.service.UserServiceDelete;
import ru.practicum.shareit.user.service.UserServiceRead;
import ru.practicum.shareit.user.service.UserServiceUpdate;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @MockBean
    UserServiceCreate userServiceCreate;
    @MockBean
    UserServiceUpdate userServiceUpdate;
    @MockBean
    UserServiceRead userServiceRead;
    @MockBean
    UserServiceDelete userServiceDelete;

    @Autowired
    private MockMvc mvc;
    @Autowired
    ObjectMapper mapper;

    private final UserDto userDto1 = new UserDto(1L, "nameUser1", "email@User1");
    private final UserDto userDto2 = new UserDto(2L, "nameUser2", "email@User2");
    private final UserDto updateUserDto1 = new UserDto(1L, "updateNameUser1", "updateEmail@User1");

    @Test
    void addUser_ok() throws Exception {
        when(userServiceCreate.addUser(userDto1))
                .thenReturn(userDto1);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto1.getName())))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail())));
        verify(userServiceCreate).addUser(userDto1);
    }

    @Test
    void updateUser_ok() throws Exception {
        when(userServiceUpdate.updateUser(1L, updateUserDto1))
                .thenReturn(updateUserDto1);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(updateUserDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updateUserDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updateUserDto1.getName())))
                .andExpect(jsonPath("$.email", is(updateUserDto1.getEmail())));
        verify(userServiceUpdate).updateUser(1L, updateUserDto1);
    }

    @Test
    void getUser_ok() throws Exception {
        when(userServiceRead.getUser(1L)).thenReturn(userDto1);

        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto1.getName())))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail())));
        verify(userServiceRead).getUser(1L);
    }

    @Test
    void findAllUsers_ok() throws Exception {
        when(userServiceRead.findAllUsers()).thenReturn(List.of(userDto1, userDto2));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].id", is(userDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(userDto2.getName())))
                .andExpect(jsonPath("$[1].email", is(userDto2.getEmail())));
        verify(userServiceRead).findAllUsers();
    }

    @Test
    void deleteUser() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
        verify(userServiceDelete).deleteUser(1L);
    }
}