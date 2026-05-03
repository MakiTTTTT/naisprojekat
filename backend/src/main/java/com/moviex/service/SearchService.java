package com.moviex.service;

import com.moviex.model.Movie;
import com.moviex.model.dto.SearchRequest;
import com.moviex.model.dto.SearchResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final MovieService movieService;
    private final EmbeddingService embeddingService;
    private final QdrantService qdrantService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Vector similarity search using Qdrant
     */
    public List<SearchResult> searchByVector(SearchRequest request) {
        List<SearchResult> results = new ArrayList<>();
        
        try {
            // Generate embedding for the query
            List<Float> queryEmbedding = embeddingService.generateEmbedding(request.getQuery());
            
            // Search in Qdrant
            List<Map<String, Object>> qdrantResults = qdrantService.searchVector(
                QdrantService.MOVIES_COLLECTION, 
                queryEmbedding,
                request.getTopK() != null ? request.getTopK() : 10,
                null
            );
            
            // If Qdrant returns results, use them
            if (!qdrantResults.isEmpty()) {
                for (Map<String, Object> qdrantResult : qdrantResults) {
                    SearchResult result = mapQdrantResultToSearchResult(qdrantResult);
                    if (result != null) {
                        results.add(result);
                    }
                }
                return results;
            }
            
            // Fallback: If Qdrant is not ready, use in-memory search
            log.warn("Qdrant search returned no results, falling back to in-memory search");
            return searchByVectorInMemory(request, queryEmbedding);
            
        } catch (Exception e) {
            log.error("Error in Qdrant vector search, falling back to in-memory", e);
            List<Float> queryEmbedding = embeddingService.generateEmbedding(request.getQuery());
            return searchByVectorInMemory(request, queryEmbedding);
        }
    }

    /**
     * In-memory vector similarity search (fallback)
     */
    private List<SearchResult> searchByVectorInMemory(SearchRequest request, List<Float> queryEmbedding) {
        List<Movie> allMovies = movieService.getAllMovies();

        return allMovies.stream()
                .map(movie -> {
                    String movieText = movie.getTitle() + " " + movie.getDescription();
                    List<Float> movieEmbedding = embeddingService.generateEmbedding(movieText);
                    float similarity = embeddingService.calculateSimilarity(queryEmbedding, movieEmbedding);

                    return SearchResult.builder()
                            .id(movie.getId())
                            .title(movie.getTitle())
                            .description(movie.getDescription())
                            .genre(movie.getGenre())
                            .year(movie.getYear())
                            .director(movie.getDirector())
                            .rating(movie.getRating())
                            .releaseDate(movie.getReleaseDate())
                            .similarity((double) similarity)
                            .build();
                })
                .sorted(Comparator.comparingDouble(SearchResult::getSimilarity).reversed())
                .limit(request.getTopK() != null ? request.getTopK() : 10)
                .collect(Collectors.toList());
    }

    /**
     * Map Qdrant result to SearchResult
     */
    private SearchResult mapQdrantResultToSearchResult(Map<String, Object> qdrantResult) {
        try {
            Map<String, Object> payload = (Map<String, Object>) qdrantResult.get("payload");
            if (payload == null) {
                return null;
            }
            
            // Extract similarity score (usually called "score" in Qdrant response)
            Double similarity = ((Number) qdrantResult.get("score")).doubleValue();
            
            return SearchResult.builder()
                    .id((String) payload.get("id"))
                    .title((String) payload.get("title"))
                    .description((String) payload.get("description"))
                    .genre((String) payload.get("genre"))
                    .year(((Number) payload.get("year")).intValue())
                    .director((String) payload.get("director"))
                    .rating(payload.get("rating") != null ? 
                            ((Number) payload.get("rating")).doubleValue() : null)
                    .similarity(similarity)
                    .build();
        } catch (Exception e) {
            log.error("Failed to map Qdrant result", e);
            return null;
        }
    }

    /**
     * Filter Search: Find movies matching specific criteria
     */
    public List<SearchResult> searchByFilter(SearchRequest request) {
        List<Movie> movies = movieService.getAllMovies();

        if (request.getGenre() != null && !request.getGenre().isEmpty()) {
            movies = movies.stream()
                    .filter(m -> m.getGenre().equalsIgnoreCase(request.getGenre()))
                    .collect(Collectors.toList());
        }

        if (request.getMinYear() != null) {
            movies = movies.stream()
                    .filter(m -> m.getYear() >= request.getMinYear())
                    .collect(Collectors.toList());
        }

        if (request.getMaxYear() != null) {
            movies = movies.stream()
                    .filter(m -> m.getYear() <= request.getMaxYear())
                    .collect(Collectors.toList());
        }

        if (request.getMinRating() != null) {
            movies = movies.stream()
                    .filter(m -> m.getRating() != null && m.getRating() >= request.getMinRating())
                    .collect(Collectors.toList());
        }

        if (request.getMaxRating() != null) {
            movies = movies.stream()
                    .filter(m -> m.getRating() != null && m.getRating() <= request.getMaxRating())
                    .collect(Collectors.toList());
        }

        return movies.stream()
                .map(movie -> SearchResult.builder()
                        .id(movie.getId())
                        .title(movie.getTitle())
                        .description(movie.getDescription())
                        .genre(movie.getGenre())
                        .year(movie.getYear())
                        .director(movie.getDirector())
                        .rating(movie.getRating())
                        .releaseDate(movie.getReleaseDate())
                        .similarity(1.0)
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Hybrid Search: Combine vector similarity with filters
     */
    public List<SearchResult> hybridSearch(SearchRequest request) {
        // Get vector search results
        List<SearchResult> vectorResults = searchByVector(request);

        // Apply filters to vector results
        List<SearchResult> filtered = vectorResults;

        if (request.getGenre() != null && !request.getGenre().isEmpty()) {
            filtered = filtered.stream()
                    .filter(r -> r.getGenre().equalsIgnoreCase(request.getGenre()))
                    .collect(Collectors.toList());
        }

        if (request.getMinYear() != null) {
            filtered = filtered.stream()
                    .filter(r -> r.getYear() >= request.getMinYear())
                    .collect(Collectors.toList());
        }

        if (request.getMaxYear() != null) {
            filtered = filtered.stream()
                    .filter(r -> r.getYear() <= request.getMaxYear())
                    .collect(Collectors.toList());
        }

        if (request.getMinRating() != null) {
            filtered = filtered.stream()
                    .filter(r -> r.getRating() != null && r.getRating() >= request.getMinRating())
                    .collect(Collectors.toList());
        }

        if (request.getMaxRating() != null) {
            filtered = filtered.stream()
                    .filter(r -> r.getRating() != null && r.getRating() <= request.getMaxRating())
                    .collect(Collectors.toList());
        }

        return filtered;
    }

    /**
     * Paginated Vector Search with iterator support
     */
    public Map<String, Object> paginatedVectorSearch(SearchRequest request) {
        List<SearchResult> allResults = searchByVector(request);

        int offset = request.getOffset() != null ? request.getOffset() : 0;
        int limit = request.getLimit() != null ? request.getLimit() : 20;

        List<SearchResult> paginated = allResults.stream()
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("results", paginated);
        response.put("total", allResults.size());
        response.put("offset", offset);
        response.put("limit", limit);
        response.put("hasMore", offset + limit < allResults.size());

        return response;
    }
}
