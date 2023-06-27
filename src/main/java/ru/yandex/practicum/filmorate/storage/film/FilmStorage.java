package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmSortByMode;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Collection<Film> getAll();

    Collection<Film> getFilmsByListIds(Collection<Long> filmIds);

    Film get(long id);

    Collection<Film> getSearchResult(String searchTerm, String by);

    Film create(Film film);

    void delete(long id);

    Film update(Film film);

    List<Long> getUsersLikedFilmsIds(long userId);

    Collection<Film> getDirectorFilmsSorted(long directorId, FilmSortByMode sortBy);
}
