package com.movierecommender.movierecommender.dto;

import lombok.*;

// MovieDto is the object we send back to the frontend for TMDB-connected endpoints.
// Fields match the format the frontend team expects.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieDto {
    private Long id;
    private Long tmdbId;
    private String title;
    private String genre;
    private Double rating;    // was voteAverage — renamed to match the frontend contract
    private Integer length;   // runtime in minutes
    private Integer year;     // release year, extracted from releaseDate
    private String age;       // age rating e.g. "PG-13", "R"
    private String poster;    // full URL to poster image
    private String overview;
    private String releaseDate;
}
