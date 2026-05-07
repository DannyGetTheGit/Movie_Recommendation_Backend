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

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final AuthService authService;

    // GET /api/movies             → returns popular movies from TMDB
    // GET /api/movies?query=dune  → searches TMDB for that title
    @GetMapping
    ResponseEntity<List<MovieDto>> getMovies(@RequestParam(required = false) String query) {
        List<MovieDto> movies = (query != null && !query.isBlank())
                ? movieService.searchMovies(query)
                : movieService.getPopularMovies();
        return ResponseEntity.ok(movies);
    }

    // GET /api/movies/{tmdbId} → full details for one movie; also records it in
    // the user's recently-viewed stack if the caller is authenticated
    @GetMapping("/{tmdbId}")
    ResponseEntity<MovieDto> getMovieDetails(@PathVariable Long tmdbId, Principal principal) {
        Long userId = (principal != null) ? resolveUserId(principal) : null;
        MovieDto movie = movieService.getMovieDetails(tmdbId, userId);
        return movie != null ? ResponseEntity.ok(movie) : ResponseEntity.notFound().build();
    }

    // GET /api/movies/recently-viewed → the last 20 movies the current user viewed
    @GetMapping("/recently-viewed")
    ResponseEntity<List<MovieDto>> getRecentlyViewed(Principal principal) {
        return ResponseEntity.ok(movieService.getRecentlyViewed(resolveUserId(principal)));
    }

    private Long resolveUserId(Principal principal) {
        User user = authService.getUserByEmail(principal.getName());
        return user.getId();
    }
}
