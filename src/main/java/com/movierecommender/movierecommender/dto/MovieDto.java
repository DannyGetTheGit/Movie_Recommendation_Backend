package com.movierecommender.movierecommender.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieDto {
    private Long id;
    private Long tmdbId;
    private String title;
    private String genre;
    private String poster;
    private String overview;
    private String releaseDate;
    private Double voteAverage;
}
