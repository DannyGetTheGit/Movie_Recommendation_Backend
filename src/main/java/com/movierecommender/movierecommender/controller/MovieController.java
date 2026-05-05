package com.movierecommender.movierecommender.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

// Simple in-memory movie controller — no database, no external API needed.
//
// @RestController        : every method returns JSON automatically
// @RequestMapping(...)   : all routes in this class start with /api/movies
@RestController
@RequestMapping("/api/movies")
public class MovieController {

    // Movie is a Java record: a compact, immutable data class.
    // Jackson (the JSON library) automatically converts it to/from JSON.
    record Movie(int id, String title, String genre, double rating) {}

    // In-memory list — lives only while the app is running.
    // ArrayList gives us fast access by index and easy add/remove.
    private final List<Movie> movies = new ArrayList<>(List.of(
            new Movie(1, "Inception",       "Sci-Fi",  8.8),
            new Movie(2, "The Dark Knight", "Action",  9.0),
            new Movie(3, "Interstellar",    "Sci-Fi",  8.6)
    ));

    private int nextId = 4;

    // ─── GET /api/movies ──────────────────────────────────────────────────────
    // Returns the full list of movies.
    @GetMapping
    List<Movie> getAllMovies() {
        return movies;
    }

    // ─── GET /api/movies/{id} ─────────────────────────────────────────────────
    // Returns one movie by its id, or 404 if not found.
    // {id} in the URL is captured by @PathVariable and passed into the method.
    @GetMapping("/{id}")
    ResponseEntity<Movie> getMovieById(@PathVariable int id) {
        return movies.stream()
                .filter(m -> m.id() == id)
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ─── POST /api/movies ─────────────────────────────────────────────────────
    // Adds a new movie. The request body must be JSON, e.g.:
    //   { "title": "Dune", "genre": "Sci-Fi", "rating": 8.0 }
    // The id field is ignored — we assign it ourselves.
    @PostMapping
    ResponseEntity<Movie> addMovie(@RequestBody Movie body) {
        Movie newMovie = new Movie(nextId++, body.title(), body.genre(), body.rating());
        movies.add(newMovie);
        return ResponseEntity.status(HttpStatus.CREATED).body(newMovie);
    }
}
