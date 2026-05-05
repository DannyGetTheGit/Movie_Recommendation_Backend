package com.movierecommender.movierecommender.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingDto {
    private Long id;
    private Long movieTmdbId;
    private String movieTitle;
    private String moviePoster;
    private Integer score;
    private LocalDateTime createdAt;
}
