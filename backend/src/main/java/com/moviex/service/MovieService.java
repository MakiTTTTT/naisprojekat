package com.moviex.service;

import com.moviex.model.Movie;
import com.moviex.repository.MovieRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final QdrantService qdrantService;
    private final EmbeddingService embeddingService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Create a new movie and sync with Qdrant
     */
    public Movie createMovie(Movie movie) {
        // Generate embedding for the movie
        String movieText = movie.getTitle() + " " + movie.getDescription();
        List<Float> embedding = embeddingService.generateEmbedding(movieText);
        
        // Store embedding as JSON string
        try {
            movie.setEmbedding(objectMapper.writeValueAsString(embedding));
        } catch (Exception e) {
            log.error("Failed to serialize embedding", e);
            movie.setEmbedding("[]");
        }
        
        // Save to PostgreSQL
        Movie savedMovie = movieRepository.save(movie);
        
        // Sync with Qdrant
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", savedMovie.getTitle());
        payload.put("description", savedMovie.getDescription());
        payload.put("genre", savedMovie.getGenre());
        payload.put("year", savedMovie.getYear());
        payload.put("director", savedMovie.getDirector());
        payload.put("rating", savedMovie.getRating());
        
        qdrantService.upsertPoint(QdrantService.MOVIES_COLLECTION, savedMovie.getId(), embedding, payload);
        
        return savedMovie;
    }

    /**
     * Get movie by ID
     */
    public Optional<Movie> getMovieById(String id) {
        return movieRepository.findById(id);
    }

    /**
     * Get all movies
     */
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    /**
     * Update a movie and sync with Qdrant
     */
    public Movie updateMovie(String id, Movie movieDetails) {
        Movie updated = movieRepository.findById(id).map(movie -> {
            if (movieDetails.getTitle() != null) {
                movie.setTitle(movieDetails.getTitle());
            }
            if (movieDetails.getDescription() != null) {
                movie.setDescription(movieDetails.getDescription());
            }
            if (movieDetails.getGenre() != null) {
                movie.setGenre(movieDetails.getGenre());
            }
            if (movieDetails.getYear() != null) {
                movie.setYear(movieDetails.getYear());
            }
            if (movieDetails.getDirector() != null) {
                movie.setDirector(movieDetails.getDirector());
            }
            if (movieDetails.getRating() != null) {
                movie.setRating(movieDetails.getRating());
            }
            if (movieDetails.getReleaseDate() != null) {
                movie.setReleaseDate(movieDetails.getReleaseDate());
            }
            
            // Regenerate embedding if description changed
            String movieText = movie.getTitle() + " " + movie.getDescription();
            List<Float> embedding = embeddingService.generateEmbedding(movieText);
            try {
                movie.setEmbedding(objectMapper.writeValueAsString(embedding));
            } catch (Exception e) {
                log.error("Failed to serialize embedding", e);
            }
            
            return movieRepository.save(movie);
        }).orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));
        
        // Sync with Qdrant
        try {
            List<Float> embedding = objectMapper.readValue(updated.getEmbedding(), 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Float.class));
            
            Map<String, Object> payload = new HashMap<>();
            payload.put("title", updated.getTitle());
            payload.put("description", updated.getDescription());
            payload.put("genre", updated.getGenre());
            payload.put("year", updated.getYear());
            payload.put("director", updated.getDirector());
            payload.put("rating", updated.getRating());
            
            qdrantService.upsertPoint(QdrantService.MOVIES_COLLECTION, updated.getId(), embedding, payload);
        } catch (Exception e) {
            log.error("Failed to sync with Qdrant during update", e);
        }
        
        return updated;
    }

    /**
     * Delete a movie and sync with Qdrant
     */
    public void deleteMovie(String id) {
        movieRepository.deleteById(id);
        qdrantService.deletePoint(QdrantService.MOVIES_COLLECTION, id);
    }

    /**
     * Search movies by genre
     */
    public List<Movie> searchByGenre(String genre) {
        return movieRepository.findByGenre(genre);
    }

    /**
     * Search movies by year range
     */
    public List<Movie> searchByYearRange(Integer minYear, Integer maxYear) {
        return movieRepository.findByYearBetween(minYear, maxYear);
    }

    /**
     * Search movies by minimum rating
     */
    public List<Movie> searchByMinRating(Double rating) {
        return movieRepository.findByRatingGreaterThanEqual(rating);
    }

    /**
     * Get all unique genres
     */
    public List<String> getAllGenres() {
        return movieRepository.findAllGenres();
    }
}
