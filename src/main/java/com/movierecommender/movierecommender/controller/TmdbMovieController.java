package com.movierecommender.movierecommender.controller;

import com.movierecommender.movierecommender.dto.MovieDto;
import com.movierecommender.movierecommender.model.User;
import com.movierecommender.movierecommender.service.AuthService;
import com.movierecommender.movierecommender.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

// Full TMDB-connected controller — activate this once you have:
//   1. A valid tmdb.api.key set in application.properties
//   2. The app running with a database (e.g. -Dspring-boot.run.profiles=h2)
//
// Mapped to /api/movies/tmdb so it does not conflict with the simple MovieController.
@RestController
@RequestMapping("/api/movies/tmdb")
@RequiredArgsConstructor
public class TmdbMovieController {

    private final MovieService movieService;
    private final AuthService authService;

    @GetMapping
    ResponseEntity<List<MovieDto>> getMovies(@RequestParam(required = false) String query) {
        List<MovieDto> movies = (query != null && !query.isBlank())
                ? movieService.searchMovies(query)
                : movieService.getPopularMovies();
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/{tmdbId}")
    ResponseEntity<MovieDto> getMovieDetails(@PathVariable Long tmdbId, Principal principal) {
        Long userId = principal != null ? resolveUserId(principal) : null;
        MovieDto movie = movieService.getMovieDetails(tmdbId, userId);
        return movie != null ? ResponseEntity.ok(movie) : ResponseEntity.notFound().build();
    }

    @GetMapping("/recently-viewed")
    ResponseEntity<List<MovieDto>> getRecentlyViewed(Principal principal) {
        Long userId = resolveUserId(principal);
        return ResponseEntity.ok(movieService.getRecentlyViewed(userId));
    }

    private Long resolveUserId(Principal principal) {
        User user = authService.getUserByEmail(principal.getName());
        return user.getId();
    }
}
