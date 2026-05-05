package com.movierecommender.movierecommender.service;

import com.movierecommender.movierecommender.dto.WatchlistItemDto;
import com.movierecommender.movierecommender.dto.WatchlistRequest;
import com.movierecommender.movierecommender.model.Movie;
import com.movierecommender.movierecommender.model.User;
import com.movierecommender.movierecommender.model.WatchlistItem;
import com.movierecommender.movierecommender.repository.UserRepository;
import com.movierecommender.movierecommender.repository.WatchlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final UserRepository userRepository;
    private final MovieService movieService;

    public List<WatchlistItemDto> getWatchlist(Long userId) {
        return watchlistRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public WatchlistItemDto addToWatchlist(Long userId, WatchlistRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Movie movie = movieService.persistMovieIfAbsent(
                movieService.getMovieDetails(request.getTmdbId(), null));

        if (watchlistRepository.existsByUserIdAndMovieId(userId, movie.getId())) {
            throw new IllegalStateException("Movie already in watchlist");
        }

        WatchlistItem item = watchlistRepository.save(
                WatchlistItem.builder().user(user).movie(movie).build());

        return toDto(item);
    }

    @Transactional
    public void removeFromWatchlist(Long userId, Long tmdbId) {
        Movie movie = movieService.persistMovieIfAbsent(
                movieService.getMovieDetails(tmdbId, null));
        watchlistRepository.deleteByUserIdAndMovieId(userId, movie.getId());
    }

    private WatchlistItemDto toDto(WatchlistItem item) {
        return WatchlistItemDto.builder()
                .id(item.getId())
                .movieTmdbId(item.getMovie().getTmdbId())
                .movieTitle(item.getMovie().getTitle())
                .moviePoster(item.getMovie().getPoster())
                .movieOverview(item.getMovie().getOverview())
                .addedAt(item.getAddedAt())
                .build();
    }
}
