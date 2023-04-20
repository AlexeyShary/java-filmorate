package ru.yandex.practicum.filmorate.validation;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validUserTest() {
        User user = getValidUser();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size(), "Валидация некорректна");
    }

    @Test
    void emailUserTest() {
        User user = getValidUser();
        user.setEmail("Matthew McConaughey");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Валидация некорректна");

        user.setEmail("MatthewMcConaughey.com");

        violations = validator.validate(user);
        assertEquals(1, violations.size(), "Валидация некорректна");
    }

    @Test
    void loginUserTest() {
        User user = getValidUser();
        user.setLogin("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Валидация некорректна");

        user.setLogin("Matthew McConaughey");

        violations = validator.validate(user);
        assertEquals(1, violations.size(), "Валидация некорректна");
    }

    @Test
    void nameUserTest() {
        User user = getValidUser();
        user.setName("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size(), "Валидация некорректна");
    }

    @Test
    void birthdayUserTest() {
        User user = getValidUser();
        user.setBirthday(LocalDate.of(2123, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Валидация некорректна");
    }

    private User getValidUser() {
        User user = new User();
        user.setEmail("Matthew@McConaughey.com");
        user.setLogin("Matthew");
        user.setName("Matthew McConaughey");
        user.setBirthday(LocalDate.of(1969, 11, 4));
        return user;
    }
}
