package com.movierecommender.movierecommender.ds;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bipartite graph connecting users ↔ movies via rating edges.
 *
 * Representation: two adjacency lists stored as HashMaps.
 *   userEdges:  userId  → [(tmdbId, score), ...]
 *   movieEdges: tmdbId  → [(userId,  score), ...]
 *
 * Space: O(R) where R = number of ratings.
 * Used by RecommendationService to find similar users without a database query.
 */
@Component
public class MovieGraph {

    private final Map<Long, List<RatingEdge>> userEdges = new HashMap<>();
    private final Map<Long, List<RatingEdge>> movieEdges = new HashMap<>();

    public void addRating(Long userId, Long tmdbId, int score) {
        upsert(userEdges, userId, tmdbId, score);
        upsert(movieEdges, tmdbId, userId, score);
    }

    public List<RatingEdge> getMoviesRatedByUser(Long userId) {
        return userEdges.getOrDefault(userId, Collections.emptyList());
    }

    public List<RatingEdge> getUsersWhoRatedMovie(Long tmdbId) {
        return movieEdges.getOrDefault(tmdbId, Collections.emptyList());
    }

    public void removeUser(Long userId) {
        List<RatingEdge> movies = userEdges.remove(userId);
        if (movies != null) {
            movies.forEach(edge -> {
                List<RatingEdge> users = movieEdges.get(edge.targetId());
                if (users != null) users.removeIf(e -> e.targetId().equals(userId));
            });
        }
    }

    private void upsert(Map<Long, List<RatingEdge>> map, Long from, Long to, int score) {
        List<RatingEdge> edges = map.computeIfAbsent(from, k -> new ArrayList<>());
        edges.removeIf(e -> e.targetId().equals(to));
        edges.add(new RatingEdge(to, score));
    }

    public record RatingEdge(Long targetId, int score) {}
}
