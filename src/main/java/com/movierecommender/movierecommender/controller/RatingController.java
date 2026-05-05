package com.movierecommender.movierecommender.controller;

import com.movierecommender.movierecommender.dto.RatingDto;
import com.movierecommender.movierecommender.dto.RatingRequest;
import com.movierecommender.movierecommender.model.User;
import com.movierecommender.movierecommender.service.AuthService;
import com.movierecommender.movierecommender.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;
    private final AuthService authService;

    @PostMapping("/ratings")
    ResponseEntity<RatingDto> rateMovie(@RequestBody RatingRequest request, Principal principal) {
        Long userId = resolveUserId(principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(ratingService.rateMovie(userId, request));
    }

    @GetMapping("/users/{userId}/ratings")
    ResponseEntity<List<RatingDto>> getUserRatings(@PathVariable Long userId) {
        return ResponseEntity.ok(ratingService.getUserRatings(userId));
    }

    private Long resolveUserId(Principal principal) {
        User user = authService.getUserByEmail(principal.getName());
        return user.getId();
    }
}
