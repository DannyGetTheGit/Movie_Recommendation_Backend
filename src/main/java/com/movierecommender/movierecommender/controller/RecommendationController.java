package com.movierecommender.movierecommender.controller;

import com.movierecommender.movierecommender.dto.MovieDto;
import com.movierecommender.movierecommender.model.User;
import com.movierecommender.movierecommender.service.AuthService;
import com.movierecommender.movierecommender.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final AuthService authService;

    @GetMapping
    ResponseEntity<List<MovieDto>> getRecommendations(Principal principal) {
        User user = authService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(recommendationService.getRecommendations(user.getId()));
    }
}
