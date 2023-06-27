package ru.yandex.practicum.filmorate.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.yandex.practicum.filmorate.model.FilmSearchByMode;
import ru.yandex.practicum.filmorate.model.FilmSortByMode;

import java.util.List;

@SuppressWarnings("ALL")
@Configuration
public class Config implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        List<Class<? extends Enum>> enums = List.of(FilmSortByMode.class);
        enums.forEach(enumClass -> registry.addConverter(String.class, enumClass,
                new EnumConverterCaseInsensetive<>(enumClass)
        ));

        registry.addConverter(String.class, FilmSearchByMode.class, new FilmSearchByModeConverter());
    }
}
