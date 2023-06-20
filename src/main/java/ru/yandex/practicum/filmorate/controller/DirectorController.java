package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    Collection<Director> getAll() { return directorService.getAll(); }

    @GetMapping("/{id}")
    Director get(@PathVariable long id) { return directorService.get(id); }

    @PostMapping
    public Director create(@Valid @RequestBody Director director) { return directorService.create(director); }

    @PutMapping
    public Director update(@RequestBody Director director) { return directorService.update(director); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) { directorService.delete(id); }
}
