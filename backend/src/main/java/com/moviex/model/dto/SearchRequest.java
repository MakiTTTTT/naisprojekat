package com.moviex.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {
    private String query;
    private Integer topK;
    private String genre;
    private Integer minYear;
    private Integer maxYear;
    private Double minRating;
    private Double maxRating;
    private Integer offset;
    private Integer limit;
}
