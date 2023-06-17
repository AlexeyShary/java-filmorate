package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = {"/schema.sql", "/filmTestData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    private final MpaDbStorage mpaDbStorage;

    @Test
    void getAll() {
        assertThat(filmDbStorage.getAll()).hasSize(2);
    }

    @Test
    void get() {
        assertThat(filmDbStorage.get(1))
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Интерстеллар")
                .hasFieldOrPropertyWithValue("description", "Крутой фильм Кристофера Нолана под крутую музыку Ханса Циммера")
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2014, 11, 6))
                .hasFieldOrPropertyWithValue("duration", 169);

        assertThat(filmDbStorage.get(1).getMpa())
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "G");
    }

    @Test
    void create() {
        Film newFilm = new Film();
        newFilm.setName("Звонок");
        newFilm.setDescription("Страшная девочка мешает людям смотреть телевизор");
        newFilm.setReleaseDate(LocalDate.of(2002, 10, 2));
        newFilm.setDuration(115);
        newFilm.setMpa(mpaDbStorage.get(5));

        filmDbStorage.create(newFilm);

        assertThat(filmDbStorage.getAll()).hasSize(3);

        assertThat(filmDbStorage.get(3))
                .hasFieldOrPropertyWithValue("id", 3L)
                .hasFieldOrPropertyWithValue("name", "Звонок")
                .hasFieldOrPropertyWithValue("description", "Страшная девочка мешает людям смотреть телевизор")
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2002, 10, 2))
                .hasFieldOrPropertyWithValue("duration", 115);

        assertThat(filmDbStorage.get(3).getMpa())
                .hasFieldOrPropertyWithValue("id", 5L)
                .hasFieldOrPropertyWithValue("name", "NC-17");
    }

    @Test
    void delete() {
        filmDbStorage.delete(1);
        assertThat(filmDbStorage.getAll()).hasSize(1);
    }

    @Test
    void update() {
        Film film = filmDbStorage.get(1);
        film.setDescription("Очень крутой фильм Кристофера Нолана под очень крутую музыку Ханса Циммера");

        filmDbStorage.update(film);

        assertThat(filmDbStorage.get(1))
                .hasFieldOrPropertyWithValue("description", "Очень крутой фильм Кристофера Нолана под очень крутую музыку Ханса Циммера");
    }
}