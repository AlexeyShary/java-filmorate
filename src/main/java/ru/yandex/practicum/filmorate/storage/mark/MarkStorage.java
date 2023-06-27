package ru.yandex.practicum.filmorate.storage.mark;

import ru.yandex.practicum.filmorate.model.Mark;

import java.util.Collection;

public interface MarkStorage {
    void addMark(long userId, long filmId, int value);

    void deleteMark(long userId, long filmId);

    Collection<Mark> getAllMarksOfFilm(long filmId);

}
