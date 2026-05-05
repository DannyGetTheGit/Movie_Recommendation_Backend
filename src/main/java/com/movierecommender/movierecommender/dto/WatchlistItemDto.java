package com.movierecommender.movierecommender.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WatchlistItemDto {
    private Long id;
    private Long movieTmdbId;
    private String movieTitle;
    private String moviePoster;
    private String movieOverview;
    private LocalDateTime addedAt;
}
