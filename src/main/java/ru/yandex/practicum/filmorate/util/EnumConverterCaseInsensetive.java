package ru.yandex.practicum.filmorate.util;

import org.springframework.core.convert.converter.Converter;


public class EnumConverterCaseInsensetive <T extends Enum<T>> implements Converter<String, T> {
    private final Class<T> enumClass;

    public EnumConverterCaseInsensetive(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public T convert(String from) {
        return T.valueOf(enumClass, from.toUpperCase());
    }
}
