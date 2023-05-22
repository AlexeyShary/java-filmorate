package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.validation.ReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class Film {
    private long id;
    @NotBlank(message = "Имя фильма не должно быть пустым.")
    private String name;
    @Length(max = 200, message = "Описание фильма не должно быть более 200 символов.")
    private String description;
    @ReleaseDate(message = "Дата релиза фильма должна быть не раньше 28 декабря 1895 года.")
    private LocalDate releaseDate;
    @Positive(message = "Длительность фильма должна быть положительной.")
    private Integer duration;
    private Set<Long> likedUsersIds = new HashSet<>();
    private LinkedHashSet<Genre> genres = new LinkedHashSet<>();
    private Mpa mpa;
}