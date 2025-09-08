package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
class UserTests {

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    User user;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void prepare() {
        user = new User(null, "example@ex.ru", "login", "name", LocalDate.of(2002, 5, 15));
    }

    private ResultActions performPost(String body) throws Exception {
        return mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(body));
    }

    @Test
    public void getUsers() throws Exception {
        mockMvc.perform(get("/users")).andExpect(status().isOk());
    }

    @Test
    public void addUserHappyPath() throws Exception {
        String json = mapper.writeValueAsString(user);
        performPost(json).andDo(print()).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void shouldBeErrorWhenEmailIsEmpty() throws Exception {
        user.setEmail("");
        String json = mapper.writeValueAsString(user);
        performPost(json).andExpect(status().isBadRequest()).andDo(print());
    }

    @Test
    public void shouldBeErrorWhenEmailIsIncorrect() throws Exception {
        user.setEmail("rr ee.ru");
        String json = mapper.writeValueAsString(user);
        performPost(json).andExpect(status().isBadRequest()).andDo(print());
    }

    @Test
    public void shouldBeErrorWhenLoginContainsSpaces() throws Exception {
        user.setLogin("lo gin");
        String json = mapper.writeValueAsString(user);
        performPost(json).andExpect(status().isBadRequest()).andDo(print());
    }

    @Test
    public void shouldBeErrorWhenLoginIsBlank() throws Exception {
        user.setLogin("");
        String json = mapper.writeValueAsString(user);
        performPost(json).andExpect(status().isBadRequest()).andDo(print());
    }

    @Test
    public void shouldBeErrorWhenBirthdayInTheFuture() throws Exception {
        user.setBirthday(LocalDate.now().plusDays(1));
        String json = mapper.writeValueAsString(user);
        performPost(json).andExpect(status().isBadRequest()).andDo(print());
    }

    @Test
    public void shouldBeErrorWhenUpdatingWithoutId() throws Exception {
        String json = mapper.writeValueAsString(user);
        Assertions.assertThrows(ServletException.class, () -> mockMvc.perform(put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(json)));
    }

    @Test
    public void shouldBeErrorWhenUpdatingWithWrongId() throws Exception {
        user.setId(9999);
        String json = mapper.writeValueAsString(user);
        Assertions.assertThrows(ServletException.class, () -> mockMvc.perform(put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(json)));
    }
}
