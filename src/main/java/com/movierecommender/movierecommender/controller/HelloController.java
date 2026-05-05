package com.movierecommender.movierecommender.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// Simplest possible endpoint — useful to confirm the app started correctly.
// @RestController  : marks this class as a REST API controller
// @GetMapping      : maps HTTP GET requests to this method
@RestController
public class HelloController {

    @GetMapping("/api/hello")
    String hello() {
        return "Hello from the Movie Recommender API!";
    }
}
