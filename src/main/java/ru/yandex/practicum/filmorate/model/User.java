package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.validation.NoSpaces;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
@EqualsAndHashCode
public class User {
    private int id;
    @Email(message = "E-mail должен быть корректным.")
    private String email;
    @NotBlank(message = "Логин не должен быть пустым.")
    @NoSpaces(message = "Логин не должен содержать пробелы.")
    private String login;
    private String name;
    @Past(message = "Дата рождения должна быть в прошедшем времени.")
    private LocalDate birthday;
}