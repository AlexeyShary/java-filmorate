package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review addReviews(@Valid @RequestBody Review review) {
        log.info("Отправлен запрос в сервис на добавление review.");
        return reviewService.addReviews(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        log.info("Отправлен запрос в сервис на обновление review.");
        return reviewService.update(review);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Integer reviewId, @PathVariable Integer userId) {
        log.info("Отправлен запрос в сервис на добавление like.");
        reviewService.addLike(reviewId, userId, true);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable("id") Integer reviewId, @PathVariable Integer userId) {
        log.info("Отправлен запрос в сервис на добавление dislike.");
        reviewService.addLike(reviewId, userId, false);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable("id") Integer reviewId) {
        log.info("Отправлен запрос в сервис на удаление review.");
        reviewService.delete(reviewId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Integer reviewId, @PathVariable Integer userId) {
        log.info("Отправлен запрос в сервис на удаление like.");
        reviewService.deleteLike(reviewId, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable("id") Integer reviewId, @PathVariable Integer userId) {
        log.info("Отправлен запрос в сервис на удаление dislike.");
        reviewService.deleteLike(reviewId, userId);
    }

    @GetMapping("/{id}")
    public Review get(@PathVariable("id") Integer reviewId) {
        log.info("Отправлен запрос в сервис на получение review по id.");
        return reviewService.get(reviewId);
    }

    @GetMapping
    public List<Review> getAll(@RequestParam(required = false) Integer filmId, @RequestParam(defaultValue = "10") Integer count) {
        log.info("Отправлен запрос в сервис на получение всех review у film.");
        return reviewService.getAll(filmId, count);
    }
}