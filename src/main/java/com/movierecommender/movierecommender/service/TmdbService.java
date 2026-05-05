package com.movierecommender.movierecommender.service;

import com.movierecommender.movierecommender.ds.MovieCache;
import com.movierecommender.movierecommender.dto.MovieDto;
import com.movierecommender.movierecommender.dto.TmdbMovieResponse;
import com.movierecommender.movierecommender.dto.TmdbSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TmdbService {

    private final RestTemplate restTemplate;
    private final MovieCache movieCache;

    @Value("${tmdb.api.key}")
    private String apiKey;

    @Value("${tmdb.api.base-url}")
    private String baseUrl;

    private static final String POSTER_BASE = "https://image.tmdb.org/t/p/w500";

    public List<MovieDto> searchMovies(String query) {
        TmdbSearchResponse response = restTemplate.getForObject(
                baseUrl + "/search/movie?api_key={key}&query={query}",
                TmdbSearchResponse.class, apiKey, query);
        if (response == null || response.getResults() == null) return Collections.emptyList();

        return response.getResults().stream()
                .map(this::toMovieDto)
                .collect(Collectors.toList());
    }

    public List<MovieDto> getPopularMovies() {
        TmdbSearchResponse response = restTemplate.getForObject(
                baseUrl + "/movie/popular?api_key={key}",
                TmdbSearchResponse.class, apiKey);
        if (response == null || response.getResults() == null) return Collections.emptyList();

        return response.getResults().stream()
                .map(this::toMovieDto)
                .collect(Collectors.toList());
    }

    public MovieDto getMovieById(Long tmdbId) {
        // HashMap cache check — avoids a network call on repeated lookups
        if (movieCache.contains(tmdbId)) {
            return movieCache.get(tmdbId);
        }

        TmdbMovieResponse response = restTemplate.getForObject(
                baseUrl + "/movie/{id}?api_key={key}",
                TmdbMovieResponse.class, tmdbId, apiKey);
        if (response == null) return null;

        MovieDto dto = toMovieDtoWithGenres(response);
        movieCache.put(tmdbId, dto);
        return dto;
    }

    private MovieDto toMovieDto(TmdbMovieResponse r) {
        return MovieDto.builder()
                .tmdbId(r.getId())
                .title(r.getTitle())
                .overview(r.getOverview())
                .releaseDate(r.getReleaseDate())
                .voteAverage(r.getVoteAverage())
                .poster(r.getPosterPath() != null ? POSTER_BASE + r.getPosterPath() : null)
                .build();
    }

    private MovieDto toMovieDtoWithGenres(TmdbMovieResponse r) {
        String genres = "";
        if (r.getGenres() != null) {
            genres = r.getGenres().stream()
                    .map(TmdbMovieResponse.TmdbGenre::getName)
                    .collect(Collectors.joining(", "));
        }
        return MovieDto.builder()
                .tmdbId(r.getId())
                .title(r.getTitle())
                .genre(genres)
                .overview(r.getOverview())
                .releaseDate(r.getReleaseDate())
                .voteAverage(r.getVoteAverage())
                .poster(r.getPosterPath() != null ? POSTER_BASE + r.getPosterPath() : null)
                .build();
    }
}
