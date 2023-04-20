package ru.yandex.practicum.filmorate.validation;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validFilmTest() {
        Film film = getValidFilm();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0, violations.size(), "Валидация некорректна");
    }

    @Test
    void emptyNameFilmTest() {
        Film film = getValidFilm();
        film.setName("");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Валидация некорректна");
    }

    @Test
    void longDescriptionFilmTest() {
        Film film = getValidFilm();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("a".repeat(200));
        film.setDescription(stringBuilder.toString());

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0, violations.size(), "Валидация некорректна");

        stringBuilder = new StringBuilder();
        stringBuilder.append("a".repeat(201));
        film.setDescription(stringBuilder.toString());

        violations = validator.validate(film);
        assertEquals(1, violations.size(), "Валидация некорректна");
    }

    @Test
    void releaseDateFilmTest() {
        Film film = getValidFilm();

        film.setReleaseDate(LocalDate.of(1895, 12, 28));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0, violations.size(), "Валидация некорректна");

        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        violations = validator.validate(film);
        assertEquals(1, violations.size(), "Валидация некорректна");
    }

    private Film getValidFilm() {
        Film film = new Film();
        film.setName("Interstellar");
        film.setDescription("A group of explorers make use of a newly discovered wormhole to surpass the limitations on.");
        film.setReleaseDate(LocalDate.of(2014, 10, 26));
        film.setDuration(169);
        return film;
    }
}
