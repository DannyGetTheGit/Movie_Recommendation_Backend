package com.movierecommender.movierecommender.ds;

import com.movierecommender.movierecommender.dto.MovieDto;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Per-user LIFO stack of recently viewed movies backed by ArrayDeque.
 * Most recently viewed movie is always at the top (index 0 of the list).
 */
@Component
public class RecentlyViewedStack {

    private static final int MAX_SIZE = 20;

    // userId → stack of recently viewed movies
    private final Map<Long, Deque<MovieDto>> stacks = new HashMap<>();

    public void push(Long userId, MovieDto movie) {
        Deque<MovieDto> stack = stacks.computeIfAbsent(userId, k -> new ArrayDeque<>());

        // Remove duplicate so the movie moves to the top on re-visit
        stack.removeIf(m -> m.getTmdbId().equals(movie.getTmdbId()));
        stack.push(movie);

        // Drop the oldest entry when the stack exceeds capacity
        if (stack.size() > MAX_SIZE) {
            ((ArrayDeque<MovieDto>) stack).pollLast();
        }
    }

    public List<MovieDto> getHistory(Long userId) {
        return List.copyOf(stacks.getOrDefault(userId, new ArrayDeque<>()));
    }

    public MovieDto peek(Long userId) {
        Deque<MovieDto> stack = stacks.get(userId);
        return (stack != null && !stack.isEmpty()) ? stack.peek() : null;
    }

    public void clearHistory(Long userId) {
        stacks.remove(userId);
    }
}
