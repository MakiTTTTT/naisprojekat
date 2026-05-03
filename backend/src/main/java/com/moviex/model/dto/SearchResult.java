package com.moviex.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResult {
    private String id;
    private String title;
    private String description;
    private String genre;
    private Integer year;
    private String director;
    private Double rating;
    private LocalDate releaseDate;
    private Double similarity;
}
