package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.validation.ReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.*;

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
    private Set<Genre> genres = new TreeSet<>(Comparator.comparingLong(Genre::getId));
    private Mpa mpa;
    private List<Director> directors = new ArrayList<>();
    private List<Integer> marks = new ArrayList<>();
    private double rating;

    @JsonSetter
    public void setGenres(Set<Genre> genres) {
        this.genres.clear();
        this.genres.addAll(genres);
    }

    @JsonSetter
    public void setRating() {
        OptionalDouble avgMark = marks.stream()
                .mapToInt(Integer::intValue)
                .average();
        this.rating = avgMark.orElse(0);
    }
}