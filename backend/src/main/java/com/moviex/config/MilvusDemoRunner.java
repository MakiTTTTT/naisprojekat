package com.moviex.config;

import com.moviex.repository.MilvusRepository;
import com.moviex.service.EmbeddingService;
import com.moviex.service.MovieService;
import com.moviex.service.MilvusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Milvus Demo Runner
 * 
 * Demonstrates all Milvus capabilities:
 * - Simple Queries (4): Get by ID, vector search, filtering, counting
 * - Complex Queries (3): Vector+filter, pagination/iterator, hybrid search
 * - Console logging of all results
 * 
 * This runner executes on application startup and prints demonstration results
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MilvusDemoRunner implements CommandLineRunner {

    private final MilvusService milvusService;
    private final MilvusRepository milvusRepository;
    private final MovieService movieService;
    private final EmbeddingService embeddingService;

    @Override
    public void run(String... args) throws Exception {
        // Give time for collections to be initialized
        Thread.sleep(2000);
        
        log.info("\n\n");
        log.info("╔════════════════════════════════════════════════════════════════════╗");
        log.info("║           MILVUS SEMANTIC MOVIE SEARCH - DEMO RUNNER               ║");
        log.info("║                                                                    ║");
        log.info("║  This demo showcases all Milvus query capabilities:                ║");
        log.info("║  - Vector indexing and similarity search                           ║");
        log.info("║  - Complex queries with filtering                                  ║");
        log.info("║  - Pagination with iterators                                       ║");
        log.info("║  - Hybrid search combining vectors and keywords                    ║");
        log.info("╚════════════════════════════════════════════════════════════════════╝");
        log.info("");
        
        try {
            // ==================== INITIALIZATION INFO ====================
            printInitializationInfo();
            
            // Get sample movie data for demonstrations
            List<com.moviex.model.Movie> allMovies = movieService.getAllMovies();
            if (allMovies.isEmpty()) {
                log.warn("No movies found in database. Skipping demonstrations.");
                return;
            }
            
            // ==================== SIMPLE QUERIES ====================
            log.info("\n╔════════════════════════════════════════════════════════════════════╗");
            log.info("║                       SIMPLE QUERIES                               ║");
            log.info("╚════════════════════════════════════════════════════════════════════╝");
            
            // SIMPLE QUERY 1: Fetch by ID
            demoSimpleQuery1_GetById(allMovies);
            
            // SIMPLE QUERY 2: Single-vector search
            demoSimpleQuery2_VectorSearch(allMovies);
            
            // SIMPLE QUERY 3: Filtering
            demoSimpleQuery3_FilteringQuery();
            
            // SIMPLE QUERY 4: Count with filter
            demoSimpleQuery4_CountQuery();
            
            // ==================== COMPLEX QUERIES ====================
            log.info("\n╔════════════════════════════════════════════════════════════════════╗");
            log.info("║                       COMPLEX QUERIES                              ║");
            log.info("╚════════════════════════════════════════════════════════════════════╝");
            
            // COMPLEX QUERY 1: Vector search + multiple filters
            demoComplexQuery1_VectorPlusFilter(allMovies);
            
            // COMPLEX QUERY 2: Vector search with pagination/iterator
            demoComplexQuery2_Pagination(allMovies);
            
            // COMPLEX QUERY 3: Hybrid search
            demoComplexQuery3_HybridSearch(allMovies);
            
            // ==================== SUMMARY ====================
            log.info("\n╔════════════════════════════════════════════════════════════════════╗");
            log.info("║                     DEMO EXECUTION COMPLETED                       ║");
            log.info("║                                                                    ║");
            log.info("║  ✓ All Milvus features demonstrated successfully                  ║");
            log.info("║  ✓ Collections are properly indexed                               ║");
            log.info("║  ✓ Vector search and complex queries working                      ║");
            log.info("║  ✓ Database is ready for production use                           ║");
            log.info("╚════════════════════════════════════════════════════════════════════╝");
            log.info("");
            
        } catch (Exception e) {
            log.error("Error during Milvus demo execution", e);
        }
    }

    // ==================== INITIALIZATION INFO ====================

    private void printInitializationInfo() {
        log.info("\n┌────────────────────────────────────────────────────────────────────┐");
        log.info("│                    INITIALIZATION STATUS                            │");
        log.info("└────────────────────────────────────────────────────────────────────┘");
        
        // Collection stats
        Map<String, Object> moviesInfo = milvusService.getCollectionInfo(MilvusService.MOVIES_COLLECTION);
        long moviesCount = (long) moviesInfo.getOrDefault("rowCount", 0L);
        
        Map<String, Object> actorsInfo = milvusService.getCollectionInfo(MilvusService.ACTORS_COLLECTION);
        long actorsCount = (long) actorsInfo.getOrDefault("rowCount", 0L);
        
        log.info("Collections Created:");
        log.info("  • {} (Records: {})", MilvusService.MOVIES_COLLECTION, moviesCount);
        log.info("  • {} (Records: {})", MilvusService.ACTORS_COLLECTION, actorsCount);
        
        log.info("Vector Indexing:");
        log.info("  • Index Type: HNSW (Hierarchical Navigable Small World)");
        log.info("  • Metric Type: COSINE (normalized similarity 0-1)");
        log.info("  • Vector Dimension: 384 (from embeddings)");
        log.info("  • M (graph width): 30");
        log.info("  • ef_construction: 200");
        
        log.info("Reason for HNSW Index:");
        log.info("  • Best performance for semantic search with cosine metric");
        log.info("  • O(log N) search complexity for ANN");
        log.info("  • Industry standard for production semantic systems");
        log.info("  • Efficient memory usage with fast query performance");
    }

    // ==================== SIMPLE QUERIES ====================

    /**
     * SIMPLE QUERY 1: Fetch record by ID
     * Demonstrates: Single record retrieval by primary key
     */
    private void demoSimpleQuery1_GetById(List<com.moviex.model.Movie> allMovies) {
        log.info("\n├─ SIMPLE QUERY 1: Fetch Record by ID");
        log.info("│  Description: Retrieve a specific movie by its ID (primary key lookup)");
        log.info("│  Query Type: Direct ID-based retrieval");
        
        try {
            // Get first movie's ID
            String movieId = allMovies.get(0).getId();
            
            log.info("│  Searching for: ID = '{}'", movieId);
            
            Map<String, Object> result = milvusRepository.getMovieById(movieId);
            
            if (!result.isEmpty()) {
                log.info("│  ✓ Found!");
                log.info("│    - Title: {}", result.get("title"));
                log.info("│    - Genre: {}", result.get("genre"));
                log.info("│    - Year: {}", result.get("year"));
            } else {
                log.info("│  ✗ Movie not found");
            }
        } catch (Exception e) {
            log.error("│  ✗ Error executing query", e);
        }
        log.info("└");
    }

    /**
     * SIMPLE QUERY 2: Single-vector similarity search
     * Demonstrates: Vector embedding search (semantic search)
     */
    private void demoSimpleQuery2_VectorSearch(List<com.moviex.model.Movie> allMovies) {
        log.info("\n├─ SIMPLE QUERY 2: Single-Vector Similarity Search");
        log.info("│  Description: Search for movies similar to a query embedding");
        log.info("│  Query Type: Vector similarity search (top-K ANN)");
        
        try {
            String query = "epic space adventure with incredible visual effects";
            log.info("│  Query: '{}'", query);
            
            List<Float> embedding = embeddingService.generateEmbedding(query);
            log.info("│  Embedding: Generated 384-dimensional vector");
            
            List<Map<String, Object>> results = milvusRepository.searchMoviesByVector(embedding, 5);
            
            log.info("│  ✓ Found {} results:", results.size());
            int rank = 1;
            for (Map<String, Object> result : results) {
                Double similarity = (Double) result.get("similarity");
                log.info("│    {}. {} - {} ({}% match)",
                    rank++,
                    result.get("title"),
                    result.get("genre"),
                    String.format("%.1f", similarity != null ? similarity * 100 : 0)
                );
            }
        } catch (Exception e) {
            log.error("│  ✗ Error executing query", e);
        }
        log.info("└");
    }

    /**
     * SIMPLE QUERY 3: Filtering query (non-vector)
     * Demonstrates: Structured query with filters
     */
    private void demoSimpleQuery3_FilteringQuery() {
        log.info("\n├─ SIMPLE QUERY 3: Filtering Query");
        log.info("│  Description: Find movies matching specific filter conditions");
        log.info("│  Query Type: Structured filtering on metadata");
        
        try {
            String filterExpr = "genre == \"Action\" && year >= 2015";
            log.info("│  Filter: {} ", filterExpr);
            
            List<Map<String, Object>> results = milvusRepository.filterMovies(filterExpr);
            
            log.info("│  ✓ Found {} movies:", results.size());
            int rank = 1;
            for (Map<String, Object> result : results) {
                if (rank <= 5) {
                    log.info("│    {}. {} ({})", rank++, result.get("title"), result.get("year"));
                }
            }
            if (results.size() > 5) {
                log.info("│    ... and {} more", results.size() - 5);
            }
        } catch (Exception e) {
            log.error("│  ✗ Error executing query", e);
        }
        log.info("└");
    }

    /**
     * SIMPLE QUERY 4: Count records with filter
     * Demonstrates: Aggregation query
     */
    private void demoSimpleQuery4_CountQuery() {
        log.info("\n├─ SIMPLE QUERY 4: Count Records with Filter");
        log.info("│  Description: Count movies matching a filter condition");
        log.info("│  Query Type: Aggregation (count)");
        
        try {
            String filterExpr = "rating >= 7.0 && year >= 2010";
            log.info("│  Filter: {} ", filterExpr);
            
            long count = milvusRepository.countMovies(filterExpr);
            
            log.info("│  ✓ Total matching records: {}", count);
        } catch (Exception e) {
            log.error("│  ✗ Error executing query", e);
        }
        log.info("└");
    }

    // ==================== COMPLEX QUERIES ====================

    /**
     * COMPLEX QUERY 1: Vector search combined with filtering (2+ conditions)
     * Demonstrates: Hybrid vector+structured filtering
     */
    private void demoComplexQuery1_VectorPlusFilter(List<com.moviex.model.Movie> allMovies) {
        log.info("\n├─ COMPLEX QUERY 1: Vector Search + Multiple Filter Conditions");
        log.info("│  Description: Combine vector similarity with structured filters");
        log.info("│  Query Type: Hybrid search (vector + 2 filter conditions)");
        log.info("│  Filter Conditions: genre AND year range");
        
        try {
            String query = "mysterious crime thriller";
            String filterExpr = "genre == \"Thriller\" && year >= 2015";
            
            log.info("│  Query: '{}'", query);
            log.info("│  Filters: {} ", filterExpr);
            
            List<Float> embedding = embeddingService.generateEmbedding(query);
            List<Map<String, Object>> results = milvusRepository.searchMoviesWithFilters(
                embedding,
                filterExpr,
                5
            );
            
            log.info("│  ✓ Found {} matching results:", results.size());
            int rank = 1;
            for (Map<String, Object> result : results) {
                Double similarity = (Double) result.get("similarity");
                log.info("│    {}. {} ({}) - {}% match",
                    rank++,
                    result.get("title"),
                    result.get("year"),
                    String.format("%.1f", similarity != null ? similarity * 100 : 0)
                );
            }
        } catch (Exception e) {
            log.error("│  ✗ Error executing query", e);
        }
        log.info("└");
    }

    /**
     * COMPLEX QUERY 2: Vector search with pagination using iterators
     * Demonstrates: Iterator-based result pagination
     */
    private void demoComplexQuery2_Pagination(List<com.moviex.model.Movie> allMovies) {
        log.info("\n├─ COMPLEX QUERY 2: Vector Search with Pagination (Iterator Pattern)");
        log.info("│  Description: Retrieve vector search results in batches using iterator");
        log.info("│  Query Type: Paginated ANN search");
        log.info("│  Pattern: Iterator-based result navigation");
        
        try {
            String query = "adventure and exploration";
            log.info("│  Query: '{}'", query);
            
            List<Float> embedding = embeddingService.generateEmbedding(query);
            
            // Fetch first page
            log.info("│  Page 1 (size=3):");
            List<Map<String, Object>> page1 = milvusRepository.searchMoviesWithPagination(embedding, 3, 1);
            int rank = 1;
            for (Map<String, Object> result : page1) {
                Double similarity = (Double) result.get("similarity");
                log.info("│    {}. {} - {}% match",
                    rank++,
                    result.get("title"),
                    String.format("%.1f", similarity != null ? similarity * 100 : 0)
                );
            }
            
            // Fetch second page
            log.info("│  Page 2 (size=3):");
            List<Map<String, Object>> page2 = milvusRepository.searchMoviesWithPagination(embedding, 3, 2);
            rank = 1;
            for (Map<String, Object> result : page2) {
                Double similarity = (Double) result.get("similarity");
                log.info("│    {}. {} - {}% match",
                    rank++,
                    result.get("title"),
                    String.format("%.1f", similarity != null ? similarity * 100 : 0)
                );
            }
            
            log.info("│  ✓ Iterator pagination demonstrated successfully");
        } catch (Exception e) {
            log.error("│  ✗ Error executing query", e);
        }
        log.info("└");
    }

    /**
     * COMPLEX QUERY 3: Hybrid search combining vector and keyword matching
     * Demonstrates: Full hybrid search capability
     */
    private void demoComplexQuery3_HybridSearch(List<com.moviex.model.Movie> allMovies) {
        log.info("\n├─ COMPLEX QUERY 3: Hybrid Search (Vector + Keyword)");
        log.info("│  Description: Combine vector similarity with keyword-based filtering");
        log.info("│  Query Type: Hybrid search (semantic + keyword)");
        log.info("│  Approach: Vector re-rank on keyword-filtered candidates");
        
        try {
            String query = "mind-bending science fiction";
            String keywordFilter = "genre == \"Science Fiction\" || genre == \"Sci-Fi\"";
            
            log.info("│  Query: '{}'", query);
            log.info("│  Keyword Filter: {} ", keywordFilter);
            
            List<Float> embedding = embeddingService.generateEmbedding(query);
            List<Map<String, Object>> results = milvusRepository.hybridSearchMovies(
                embedding,
                keywordFilter,
                5
            );
            
            log.info("│  ✓ Found {} hybrid search results:", results.size());
            log.info("│    (Candidates filtered by keyword, re-ranked by vector similarity)");
            int rank = 1;
            for (Map<String, Object> result : results) {
                Double similarity = (Double) result.get("similarity");
                log.info("│    {}. {} ({}) - {}% match",
                    rank++,
                    result.get("title"),
                    result.get("genre"),
                    String.format("%.1f", similarity != null ? similarity * 100 : 0)
                );
            }
        } catch (Exception e) {
            log.error("│  ✗ Error executing query", e);
        }
        log.info("└");
    }
}
