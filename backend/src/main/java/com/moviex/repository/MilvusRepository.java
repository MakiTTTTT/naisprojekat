package com.moviex.repository;

import com.moviex.service.MilvusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Milvus Repository Layer
 * Provides type-safe data access for movies and actors collections
 * Encapsulates Milvus operations for use by service layer
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class MilvusRepository {

    private final MilvusService milvusService;

    // ==================== Movie Collection Operations ====================

    /**
     * Insert a movie into the movies collection
     */
    public long insertMovie(Map<String, Object> movie) {
        List<Map<String, Object>> movies = Collections.singletonList(movie);
        return milvusService.insertDocuments(MilvusService.MOVIES_COLLECTION, movies);
    }
    public void deleteMovie(String id) {
        milvusService.deleteById(MilvusService.MOVIES_COLLECTION, id);
    }

    /**
     * Query a movie by ID
     */
    public Map<String, Object> getMovieById(String movieId) {
        return milvusService.queryById(MilvusService.MOVIES_COLLECTION, movieId);
    }

    /**
     * Search movies by vector embedding
     */
    public List<Map<String, Object>> searchMoviesByVector(List<Float> embedding, int topK) {
        return milvusService.vectorSearch(MilvusService.MOVIES_COLLECTION, embedding, topK);
    }

    /**
     * Filter movies by expression
     * Example: "genre == \"Action\" and year >= 2020"
     */
    public List<Map<String, Object>> filterMovies(String filterExpression) {
        return milvusService.filterQuery(MilvusService.MOVIES_COLLECTION, filterExpression, 
            Arrays.asList("*"));
    }

    /**
     * Count movies matching filter
     */
    public long countMovies(String filterExpression) {
        return milvusService.countByFilter(MilvusService.MOVIES_COLLECTION, filterExpression);
    }

    /**
     * Search movies with vector + multiple filters
     * Example: Find action movies from 2020+ similar to embedding
     */
    public List<Map<String, Object>> searchMoviesWithFilters(List<Float> embedding, 
                                                              String filterExpression, 
                                                              int topK) {
        return milvusService.vectorSearchWithMultipleFilters(
            MilvusService.MOVIES_COLLECTION, 
            embedding, 
            filterExpression, 
            topK
        );
    }

    /**
     * Search movies with pagination/iterator
     */
    public List<Map<String, Object>> searchMoviesWithPagination(
        List<Float> vector,
        int page,
        int pageSize) {

    return searchMoviesWithPagination(vector, "", page, pageSize);
    }

    /**
     * Hybrid search for movies (vector search across both collections)
     */
    public Map<String, List<Map<String, Object>>> hybridSearchMovies(List<Float> embedding, 
                                                                      int topK) {
        return milvusService.hybridVectorSearch(embedding, topK);
    }

    /**
     * Get movie collection info
     */
    public Map<String, Object> getMovieInfo() {
        return milvusService.getCollectionInfo(MilvusService.MOVIES_COLLECTION);
    }

    /**
     * Get number of movies
     */
    public long getMovieCount() {
        return milvusService.getCollectionRowCount(MilvusService.MOVIES_COLLECTION);
    }

    // ==================== Actor Collection Operations ====================

    /**
     * Insert an actor into the actors collection
     */
    public long insertActor(Map<String, Object> actor) {
        List<Map<String, Object>> actors = Collections.singletonList(actor);
        return milvusService.insertDocuments(MilvusService.ACTORS_COLLECTION, actors);
    }

    /**
     * Query an actor by ID
     */
    public Map<String, Object> getActorById(String actorId) {
        return milvusService.queryById(MilvusService.ACTORS_COLLECTION, actorId);
    }

    /**
     * Search actors by vector embedding
     */
    public List<Map<String, Object>> searchActorsByVector(List<Float> embedding, int topK) {
        return milvusService.vectorSearch(MilvusService.ACTORS_COLLECTION, embedding, topK);
    }

    /**
     * Filter actors by expression
     */
    public List<Map<String, Object>> filterActors(String filterExpression) {
        return milvusService.filterQuery(MilvusService.ACTORS_COLLECTION, filterExpression,
            Arrays.asList("*"));
    }

    /**
     * Count actors matching filter
     */
    public long countActors(String filterExpression) {
        return milvusService.countByFilter(MilvusService.ACTORS_COLLECTION, filterExpression);
    }

    /**
     * Search actors with vector + multiple filters
     */
    public List<Map<String, Object>> searchActorsWithFilters(List<Float> embedding, 
                                                              String filterExpression, 
                                                              int topK) {
        return milvusService.vectorSearchWithMultipleFilters(
            MilvusService.ACTORS_COLLECTION, 
            embedding, 
            filterExpression, 
            topK
        );
    }

    /**
     * Search actors with pagination/iterator
     */
    public List<Map<String, Object>> searchActorsWithPagination(List<Float> embedding, 
                                                                 String filterExpr,
                                                                 int pageSize, 
                                                                 int pageNumber) {
        return milvusService.vectorSearchWithIterator(
            MilvusService.ACTORS_COLLECTION, 
            embedding, 
            filterExpr,
            pageSize, 
            pageNumber
        );
    }

    /**
     * Hybrid search for actors (vector search across both collections)
     */
    public Map<String, List<Map<String, Object>>> hybridSearchActors(List<Float> embedding, 
                                                                      int topK) {
        return milvusService.hybridVectorSearch(embedding, topK);
    }

    /**
     * Get actor collection info
     */
    public Map<String, Object> getActorInfo() {
        return milvusService.getCollectionInfo(MilvusService.ACTORS_COLLECTION);
    }

    /**
     * Get number of actors
     */
    public long getActorCount() {
        return milvusService.getCollectionRowCount(MilvusService.ACTORS_COLLECTION);
    }
}

