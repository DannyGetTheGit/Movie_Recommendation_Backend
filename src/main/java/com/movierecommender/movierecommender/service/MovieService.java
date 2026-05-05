package com.movierecommender.movierecommender.service;

import com.movierecommender.movierecommender.ds.RecentlyViewedStack;
import com.movierecommender.movierecommender.dto.MovieDto;
import com.movierecommender.movierecommender.model.Movie;
import com.movierecommender.movierecommender.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final TmdbService tmdbService;
    private final MovieRepository movieRepository;
    private final RecentlyViewedStack recentlyViewedStack;

    public List<MovieDto> searchMovies(String query) {
        return tmdbService.searchMovies(query);
    }

    public List<MovieDto> getPopularMovies() {
        return tmdbService.getPopularMovies();
    }

    public MovieDto getMovieDetails(Long tmdbId, Long userId) {
        MovieDto movie = tmdbService.getMovieById(tmdbId);
        if (movie != null && userId != null) {
            // Push to per-user stack so /recently-viewed always reflects the real browse order
            recentlyViewedStack.push(userId, movie);
            persistMovieIfAbsent(movie);
        }
        return movie;
    }

    public List<MovieDto> getRecentlyViewed(Long userId) {
        return recentlyViewedStack.getHistory(userId);
    }

    // Ensures a TMDB movie exists in our DB before we attach ratings/watchlist to it
    public Movie persistMovieIfAbsent(MovieDto dto) {
        return movieRepository.findByTmdbId(dto.getTmdbId())
                .orElseGet(() -> movieRepository.save(Movie.builder()
                        .tmdbId(dto.getTmdbId())
                        .title(dto.getTitle())
                        .genre(dto.getGenre())
                        .poster(dto.getPoster())
                        .overview(dto.getOverview())
                        .releaseDate(dto.getReleaseDate())
                        .voteAverage(dto.getVoteAverage())
                        .build()));
    }
}
