package com.movierecommender.movierecommender.dto;

import lombok.Data;

@Data
public class RatingRequest {
    private Long tmdbId;
    private Integer score;
}
