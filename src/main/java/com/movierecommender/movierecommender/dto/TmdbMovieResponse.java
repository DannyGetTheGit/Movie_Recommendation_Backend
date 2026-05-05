package com.movierecommender.movierecommender.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbMovieResponse {

    private Long id;
    private String title;
    private String overview;

    @JsonProperty("release_date")
    private String releaseDate;

    @JsonProperty("vote_average")
    private Double voteAverage;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("genre_ids")
    private List<Integer> genreIds;

    private List<TmdbGenre> genres;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TmdbGenre {
        private Integer id;
        private String name;
    }
}
