package ru.yandex.practicum.filmorate.storage.mark;

public interface MarkStorage {
    void addMark(long userId, long filmId, int value);

    void deleteMark(long userId, long filmId);

}
