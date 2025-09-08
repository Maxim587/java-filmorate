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
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
class FilmTests {

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    Film newFilm;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void prepare() {
        newFilm = new Film(null, "nameN", "descriptionNO", LocalDate.of(2002, 5, 15), 120);
    }

    private ResultActions performPost(String body) throws Exception {
        return mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(body));
    }

    @Test
    public void getFilms() throws Exception {
        mockMvc.perform(get("/films")).andExpect(status().isOk());
    }

    @Test
    public void addFilmHappyPath() throws Exception {
        String json = mapper.writeValueAsString(newFilm);
        performPost(json).andDo(print()).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void shouldBeErrorWhenNameIsEmpty() throws Exception {
        newFilm.setName("");
        String json = mapper.writeValueAsString(newFilm);
        performPost(json).andExpect(status().isBadRequest()).andDo(print());
    }

    @Test
    public void shouldBeErrorWhenDescriptionLengthOver200() throws Exception {
        newFilm.setDescription("s".repeat(201));
        String json = mapper.writeValueAsString(newFilm);
        performPost(json).andExpect(status().isBadRequest()).andDo(print());
    }

    @Test
    public void shouldBeOkWhenDescriptionLengthNotOver200() throws Exception {
        newFilm.setDescription("s".repeat(200));
        String json = mapper.writeValueAsString(newFilm);
        performPost(json).andExpect(status().is2xxSuccessful()).andDo(print());
    }

    @Test
    public void shouldBeErrorWhenReleaseDateLess_28_12_1895() throws Exception {
        newFilm.setReleaseDate(LocalDate.of(1895, 12, 27));
        String json = mapper.writeValueAsString(newFilm);
        performPost(json).andExpect(status().isBadRequest()).andDo(print());
    }

    @Test
    public void shouldBeOkWhenReleaseDateAfter_28_12_1895() throws Exception {
        newFilm.setReleaseDate(LocalDate.of(1895, 12, 28));
        String json = mapper.writeValueAsString(newFilm);
        performPost(json).andExpect(status().is2xxSuccessful()).andDo(print());
    }

    @Test
    public void shouldBeErrorWhenDurationIsNegative() throws Exception {
        newFilm.setDuration(-220);
        String json = mapper.writeValueAsString(newFilm);
        performPost(json).andExpect(status().isBadRequest()).andDo(print());
    }

    @Test
    public void shouldBeErrorWhenDurationIs0() throws Exception {
        newFilm.setDuration(0);
        String json = mapper.writeValueAsString(newFilm);
        performPost(json).andExpect(status().isBadRequest()).andDo(print());
    }

    @Test
    public void shouldBeOkWhenDurationMoreThan0() throws Exception {
        newFilm.setDuration(1);
        String json = mapper.writeValueAsString(newFilm);
        performPost(json).andExpect(status().is2xxSuccessful()).andDo(print());
    }

    @Test
    public void shouldBeErrorWhenUpdatingWithoutId() throws Exception {
        String json = mapper.writeValueAsString(newFilm);
        Assertions.assertThrows(ServletException.class, () -> mockMvc.perform(put("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(json)));
    }

    @Test
    public void shouldBeErrorWhenUpdatingWithWrongId() throws Exception {
        newFilm.setId(9999);
        String json = mapper.writeValueAsString(newFilm);
        Assertions.assertThrows(ServletException.class, () -> mockMvc.perform(put("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(json)));
    }
}
