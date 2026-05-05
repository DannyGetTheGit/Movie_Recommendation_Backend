package com.movierecommender.movierecommender.ds;

import com.movierecommender.movierecommender.dto.MovieDto;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * HashMap-based cache for TMDB API responses (tmdbId → MovieData).
 * Prevents redundant network calls for the same movie within the TTL window.
 */
@Component
public class MovieCache {

    private static final long TTL_SECONDS = 3600;

    // Key: tmdbId, Value: cached entry with expiry timestamp
    private final Map<Long, CacheEntry> cache = new HashMap<>();

    public void put(Long tmdbId, MovieDto movie) {
        cache.put(tmdbId, new CacheEntry(movie, Instant.now().getEpochSecond()));
    }

    public MovieDto get(Long tmdbId) {
        CacheEntry entry = cache.get(tmdbId);
        if (entry == null) return null;
        if (Instant.now().getEpochSecond() - entry.timestamp() > TTL_SECONDS) {
            cache.remove(tmdbId);
            return null;
        }
        return entry.movie();
    }

    public boolean contains(Long tmdbId) {
        return get(tmdbId) != null;
    }

    public void evict(Long tmdbId) {
        cache.remove(tmdbId);
    }

    private record CacheEntry(MovieDto movie, long timestamp) {}
}
