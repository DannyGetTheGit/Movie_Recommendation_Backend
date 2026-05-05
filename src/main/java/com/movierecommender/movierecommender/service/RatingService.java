package com.movierecommender.movierecommender.service;

import com.movierecommender.movierecommender.ds.MovieGraph;
import com.movierecommender.movierecommender.dto.RatingDto;
import com.movierecommender.movierecommender.dto.RatingRequest;
import com.movierecommender.movierecommender.model.Movie;
import com.movierecommender.movierecommender.model.Rating;
import com.movierecommender.movierecommender.model.User;
import com.movierecommender.movierecommender.repository.RatingRepository;
import com.movierecommender.movierecommender.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final MovieService movieService;
    private final MovieGraph movieGraph;

    @Transactional
    public RatingDto rateMovie(Long userId, RatingRequest request) {
        if (request.getScore() < 1 || request.getScore() > 10) {
            throw new IllegalArgumentException("Score must be between 1 and 10");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Movie movie = movieService.persistMovieIfAbsent(
                movieService.getMovieDetails(request.getTmdbId(), null));

        Rating rating = ratingRepository.findByUserIdAndMovieId(userId, movie.getId())
                .orElse(Rating.builder().user(user).movie(movie).build());

        rating.setScore(request.getScore());
        rating = ratingRepository.save(rating);

        // Keep the in-memory graph in sync so recommendations reflect the new rating immediately
        movieGraph.addRating(userId, movie.getTmdbId(), request.getScore());

        return toDto(rating);
    }

    public List<RatingDto> getUserRatings(Long userId) {
        return ratingRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private RatingDto toDto(Rating r) {
        return RatingDto.builder()
                .id(r.getId())
                .movieTmdbId(r.getMovie().getTmdbId())
                .movieTitle(r.getMovie().getTitle())
                .moviePoster(r.getMovie().getPoster())
                .score(r.getScore())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
