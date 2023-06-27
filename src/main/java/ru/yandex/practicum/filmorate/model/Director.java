package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class Director {
    private long id;
    @NotBlank(message = "Имя режиссера не должно быть пустым.")
    private String name;
}
