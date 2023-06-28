package ru.yandex.practicum.filmorate.storage.mark;

import java.util.Collection;

public interface MarkStorage {
    void addMark(long userId, long filmId, int value);

    void deleteMark(long userId, long filmId);

    Collection<Integer> getAllMarksOfFilm(long filmId);

}
