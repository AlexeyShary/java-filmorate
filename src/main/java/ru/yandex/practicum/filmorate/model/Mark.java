package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class Mark {
    private long id;
    @NotNull
    private long userId;
    @NotNull
    private long filmId;
    @Min(1)
    @Max(10)
    private int value;
}
