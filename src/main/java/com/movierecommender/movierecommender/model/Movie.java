package com.movierecommender.movierecommender.model;

import jakarta.persistence.*;
import lombok.*;

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
    private String poster;

    @Column(columnDefinition = "TEXT")
    private String overview;

    private String releaseDate;
    private Double voteAverage;
}
