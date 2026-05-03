package com.moviex.config;

import com.moviex.service.MovieService;
import com.moviex.service.QdrantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final QdrantService qdrantService;
    private final MovieService movieService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Initializing Qdrant collections and syncing data...");
        
        try {
            // Create Qdrant collections if they don't exist
            qdrantService.createCollectionsIfNotExist();
            log.info("Qdrant collections initialized successfully");
            
            // Sync all existing movies to Qdrant
            log.info("Syncing existing movies to Qdrant...");
            int synced = 0;
            for (var movie : movieService.getAllMovies()) {
                try {
                    // Re-create the movie to trigger embedding generation and Qdrant sync
                    // This is done by updating the movie (which triggers Qdrant upsert)
                    if (movie.getEmbedding() == null || movie.getEmbedding().isEmpty() || movie.getEmbedding().equals("[]")) {
                        movieService.updateMovie(movie.getId(), movie);
                        synced++;
                    }
                } catch (Exception e) {
                    log.warn("Failed to sync movie {} to Qdrant", movie.getId(), e);
                }
            }
            log.info("Synced {} movies to Qdrant", synced);
            
            // Verify Qdrant is healthy
            if (qdrantService.healthCheck()) {
                log.info("Qdrant health check passed");
            } else {
                log.warn("Qdrant health check failed - vector search may not work properly");
            }
        } catch (Exception e) {
            log.error("Error during data initialization", e);
        }
    }
}
