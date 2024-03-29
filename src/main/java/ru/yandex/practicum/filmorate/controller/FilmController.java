package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmSearchByMode;
import ru.yandex.practicum.filmorate.model.FilmSortByMode;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> getAll() {
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film get(@PathVariable long id) {
        return filmService.get(id);
    }

    @GetMapping("/search")
    public Collection<Film> getSearchResult(@RequestParam String query, @RequestParam FilmSearchByMode by) {
        return filmService.getSearchResult(query, by);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopular(@RequestParam(defaultValue = "10", required = false) long count,
                                       @RequestParam(value = "genreId", required = false) Long genreId,
                                       @RequestParam(value = "year", required = false) Integer year) {
        return filmService.getPopular(count, genreId, year);
    }

    @GetMapping("/common")
    public Collection<Film> getCommon(@RequestParam long userId, @RequestParam long friendId) {
        return filmService.getCommon(userId, friendId);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        filmService.delete(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id, @PathVariable long userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> getDirectorFilmsSorted(@PathVariable long directorId, @RequestParam FilmSortByMode sortBy) {
        return filmService.getDirectorFilmsSorted(directorId, sortBy);
    }
}
