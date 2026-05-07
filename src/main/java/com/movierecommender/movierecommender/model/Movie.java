package com.movierecommender.movierecommender.model;

import jakarta.persistence.*;
import lombok.*;

// JPA entity — maps to the "movies" table in the database.
// Fields mirror MovieDto so that what we store matches what we send to the frontend.
@Entity
@Table(name = "movies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long tmdbId;

    @Column(nullable = false)
    private String title;

    private String genre;
    private Double rating;    // was voteAverage — renamed to match frontend field name
    private Integer length;   // runtime in minutes
    private Integer year;     // release year
    private String age;       // age rating e.g. "PG-13"
    private String poster;

    @Column(columnDefinition = "TEXT")
    private String overview;

    private String releaseDate;
}
