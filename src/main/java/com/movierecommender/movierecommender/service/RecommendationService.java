package com.movierecommender.movierecommender.service;

import com.movierecommender.movierecommender.ds.MovieGraph;
import com.movierecommender.movierecommender.ds.MovieGraph.RatingEdge;
import com.movierecommender.movierecommender.dto.MovieDto;
import com.movierecommender.movierecommender.repository.RatingRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private static final int HIGH_SCORE_THRESHOLD = 7;
    private static final int TOP_N = 10;

    private final MovieGraph movieGraph;
    private final RatingRepository ratingRepository;
    private final TmdbService tmdbService;

    // Populate the graph from persisted ratings on startup so recommendations
    // work correctly even after a server restart.
    @PostConstruct
    void initGraph() {
        ratingRepository.findAll().forEach(r ->
                movieGraph.addRating(r.getUser().getId(), r.getMovie().getTmdbId(), r.getScore()));
    }

    public List<MovieDto> getRecommendations(Long userId) {
        // Step 1: movies this user has already rated highly
        Set<Long> userHighRated = new HashSet<>();
        Set<Long> userAllRated = new HashSet<>();
        for (RatingEdge edge : movieGraph.getMoviesRatedByUser(userId)) {
            userAllRated.add(edge.targetId());
            if (edge.score() >= HIGH_SCORE_THRESHOLD) userHighRated.add(edge.targetId());
        }

        // Fall back to popular when the user has no ratings yet
        if (userHighRated.isEmpty()) {
            return tmdbService.getPopularMovies().stream()
                    .limit(TOP_N)
                    .collect(Collectors.toList());
        }

        // Step 2: find similar users and score candidate movies via the graph.
        // Weight = (similarUser's rating on shared movie / 10) × (candidate rating / 10)
        // so both agreement strength and candidate quality matter.
        Map<Long, Double> candidateScores = new HashMap<>();

        for (Long sharedMovieId : userHighRated) {
            for (RatingEdge similarUser : movieGraph.getUsersWhoRatedMovie(sharedMovieId)) {
                if (similarUser.targetId().equals(userId)) continue;

                for (RatingEdge candidate : movieGraph.getMoviesRatedByUser(similarUser.targetId())) {
                    if (candidate.score() < HIGH_SCORE_THRESHOLD) continue;
                    if (userAllRated.contains(candidate.targetId())) continue;

                    double weight = (similarUser.score() / 10.0) * (candidate.score() / 10.0);
                    candidateScores.merge(candidate.targetId(), weight, Double::sum);
                }
            }
        }

        // Step 3: PriorityQueue to extract the top-N scored candidates efficiently.
        // Min-heap of size N — poll the lowest score when the heap overflows.
        PriorityQueue<Map.Entry<Long, Double>> minHeap = new PriorityQueue<>(
                Comparator.comparingDouble(Map.Entry::getValue));

        for (Map.Entry<Long, Double> entry : candidateScores.entrySet()) {
            minHeap.offer(entry);
            if (minHeap.size() > TOP_N) minHeap.poll();
        }

        // Step 4: sort descending and fetch movie details
        List<Map.Entry<Long, Double>> sorted = new ArrayList<>(minHeap);
        sorted.sort(Comparator.comparingDouble(Map.Entry<Long, Double>::getValue).reversed());

        List<MovieDto> recommendations = new ArrayList<>();
        for (Map.Entry<Long, Double> entry : sorted) {
            MovieDto movie = tmdbService.getMovieById(entry.getKey());
            if (movie != null) recommendations.add(movie);
        }

        // Pad with popular movies when collaborative filtering yields too few results
        if (recommendations.size() < TOP_N) {
            Set<Long> included = recommendations.stream()
                    .map(MovieDto::getTmdbId).collect(Collectors.toSet());

            for (MovieDto popular : tmdbService.getPopularMovies()) {
                if (!included.contains(popular.getTmdbId()) && !userAllRated.contains(popular.getTmdbId())) {
                    recommendations.add(popular);
                    if (recommendations.size() >= TOP_N) break;
                }
            }
        }

        return recommendations;
    }
}
