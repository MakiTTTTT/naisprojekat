package com.moviex.controller;

import com.moviex.model.Movie;
import com.moviex.model.dto.SearchRequest;
import com.moviex.model.dto.SearchResult;
import com.moviex.service.MovieService;
import com.moviex.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final SearchService searchService;

    // ==================== CRUD Operations ====================

    /**
     * Create a new movie
     */
    @PostMapping("/movies")
    public ResponseEntity<?> createMovie(@RequestBody Movie movie) {
        try {
            Movie created = movieService.createMovie(movie);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * Get movie by ID (Simple Query 1)
     */
    @GetMapping("/movies/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable String id) {
        try {
            Optional<Movie> movie = movieService.getMovieById(id);
            if (movie.isPresent()) {
                return ResponseEntity.ok(movie.get());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Movie not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * Get all movies
     */
    @GetMapping("/movies")
    public ResponseEntity<?> getAllMovies() {
        try {
            List<Movie> movies = movieService.getAllMovies();
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * Update movie
     */
    @PutMapping("/movies/{id}")
    public ResponseEntity<?> updateMovie(@PathVariable String id, @RequestBody Movie movieDetails) {
        try {
            Movie updated = movieService.updateMovie(id, movieDetails);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * Delete movie
     */
    @DeleteMapping("/movies/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable String id) {
        try {
            movieService.deleteMovie(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Movie deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    // ==================== Search Operations ====================

    /**
     * Search by genre (Simple Query 2)
     */
    @GetMapping("/movies/search/by-genre/{genre}")
    public ResponseEntity<?> searchByGenre(@PathVariable String genre) {
        try {
            List<Movie> movies = movieService.searchByGenre(genre);
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * Vector similarity search (Complex Query 1)
     */
    @PostMapping("/search/vector")
    public ResponseEntity<?> vectorSearch(@RequestBody SearchRequest request) {
        try {
            List<SearchResult> results = searchService.searchByVector(request);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("Error in vector search", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * Filter search by multiple criteria (Complex Query 2)
     */
    @PostMapping("/search/filter")
    public ResponseEntity<?> filterSearch(@RequestBody SearchRequest request) {
        try {
            List<SearchResult> results = searchService.searchByFilter(request);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("Error in filter search", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * Hybrid search combining vector + filters (Complex Query 3)
     */
    @PostMapping("/search/hybrid")
    public ResponseEntity<?> hybridSearch(@RequestBody SearchRequest request) {
        try {
            List<SearchResult> results = searchService.hybridSearch(request);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("Error in hybrid search", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * Paginated vector search (Additional Complex Query)
     */
    @GetMapping("/search/paginated")
    public ResponseEntity<?> paginatedSearch(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer limit) {
        try {
            SearchRequest request = new SearchRequest();
            request.setQuery(query);
            request.setOffset(offset);
            request.setLimit(limit);

            Map<String, Object> results = searchService.paginatedVectorSearch(request);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("Error in paginated search", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    // ==================== Utility Endpoints ====================

    /**
     * Get all available genres
     */
    @GetMapping("/genres")
    public ResponseEntity<?> getGenres() {
        try {
            List<String> genres = movieService.getAllGenres();
            return ResponseEntity.ok(genres);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "Semantic Movie Search API");
        return ResponseEntity.ok(response);
    }
}
