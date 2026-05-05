package com.movierecommender.movierecommender.controller;

import com.movierecommender.movierecommender.dto.WatchlistItemDto;
import com.movierecommender.movierecommender.dto.WatchlistRequest;
import com.movierecommender.movierecommender.model.User;
import com.movierecommender.movierecommender.service.AuthService;
import com.movierecommender.movierecommender.service.WatchlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
@RequiredArgsConstructor
public class WatchlistController {

    private final WatchlistService watchlistService;
    private final AuthService authService;

    @GetMapping
    ResponseEntity<List<WatchlistItemDto>> getWatchlist(Principal principal) {
        return ResponseEntity.ok(watchlistService.getWatchlist(resolveUserId(principal)));
    }

    @PostMapping
    ResponseEntity<WatchlistItemDto> addToWatchlist(@RequestBody WatchlistRequest request,
                                                    Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(watchlistService.addToWatchlist(resolveUserId(principal), request));
    }

    @DeleteMapping("/{tmdbId}")
    ResponseEntity<Void> removeFromWatchlist(@PathVariable Long tmdbId, Principal principal) {
        watchlistService.removeFromWatchlist(resolveUserId(principal), tmdbId);
        return ResponseEntity.noContent().build();
    }

    private Long resolveUserId(Principal principal) {
        User user = authService.getUserByEmail(principal.getName());
        return user.getId();
    }
}
