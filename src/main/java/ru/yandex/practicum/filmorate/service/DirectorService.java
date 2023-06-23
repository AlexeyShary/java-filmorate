package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
    @Qualifier("directorDbStorage")
    private final DirectorStorage directorStorage;

    public Collection<Director> getAll() {
        return directorStorage.getAll();
    }

    public Director get(long id) {
        return directorStorage.get(id);
    }

    public Director create(Director director) {
        return directorStorage.create(director);
    }

    public void delete(long id) {
        directorStorage.delete(id);
    }

    public Director update(Director director) {
        return directorStorage.update(director);
    }
}
