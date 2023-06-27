package ru.yandex.practicum.filmorate.util;

import org.springframework.core.convert.converter.Converter;
import ru.yandex.practicum.filmorate.model.FilmSearchByMode;

public class FilmSearchByModeConverter implements Converter<String, FilmSearchByMode> {
    @Override
    public FilmSearchByMode convert(String source) {
        String[] values = source.split(",");

        if (values.length > 1) {
            if (containsIgnoreCase(values, "director") && containsIgnoreCase(values, "title")) {
                return FilmSearchByMode.DIRECTOR_TITLE;
            }
        }

        return FilmSearchByMode.valueOf(source.toUpperCase());
    }

    private boolean containsIgnoreCase(String[] array, String value) {
        for (String element : array) {
            if (element.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
