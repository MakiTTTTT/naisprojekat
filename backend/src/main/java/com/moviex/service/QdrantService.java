package com.moviex.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.*;
import okhttp3.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class QdrantService {

    public static final String MOVIES_COLLECTION = "movies";
    public static final String GENRES_COLLECTION = "genres";
    public static final int VECTOR_SIZE = 384;

    @Value("${qdrant.host:localhost}")
    private String qdrantHost;

    @Value("${qdrant.port:6333}")
    private int qdrantPort;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private OkHttpClient httpClient = new OkHttpClient();

    private String getBaseUrl() {
        return "http://" + qdrantHost + ":" + qdrantPort;
    }

    /**
     * Create collections if they don't exist
     */
    public void createCollectionsIfNotExist() {
        try {
            if (!collectionExists(MOVIES_COLLECTION)) {
                createCollection(MOVIES_COLLECTION);
                log.info("Created collection: {}", MOVIES_COLLECTION);
            }
            if (!collectionExists(GENRES_COLLECTION)) {
                createCollection(GENRES_COLLECTION);
                log.info("Created collection: {}", GENRES_COLLECTION);
            }
        } catch (Exception e) {
            log.error("Error initializing collections", e);
        }
    }

    /**
     * Check if collection exists
     */
    private boolean collectionExists(String collectionName) throws Exception {
        String url = getBaseUrl() + "/collections/" + collectionName;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            return response.isSuccessful();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Create a collection with vector configuration
     */
    private void createCollection(String collectionName) throws Exception {
        String url = getBaseUrl() + "/collections";

        Map<String, Object> vectorConfig = new HashMap<>();
        vectorConfig.put("size", VECTOR_SIZE);
        vectorConfig.put("distance", "Cosine");

        Map<String, Object> payload = new HashMap<>();
        payload.put("name", collectionName);
        payload.put("vectors", vectorConfig);

        String jsonBody = objectMapper.writeValueAsString(payload);

        Request request = new Request.Builder()
                .url(url)
                .put(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("Failed to create collection: {} - {}", collectionName, response.body().string());
            }
        }
    }

    /**
     * Upsert a point (vector with metadata) into Qdrant
     */
    public void upsertPoint(String collectionName, String pointId, List<Float> vector,
                           Map<String, Object> payload) {
        try {
            String url = getBaseUrl() + "/collections/" + collectionName + "/points";

            Map<String, Object> point = new HashMap<>();
            point.put("id", Long.parseLong(pointId.hashCode() + ""));
            point.put("vector", vector);
            point.put("payload", payload);

            Map<String, Object> body = new HashMap<>();
            body.put("points", Arrays.asList(point));

            String jsonBody = objectMapper.writeValueAsString(body);

            Request request = new Request.Builder()
                    .url(url)
                    .put(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.warn("Failed to upsert point {} to {}: {}", pointId, collectionName, response.message());
                }
            }
        } catch (Exception e) {
            log.error("Error upserting point to Qdrant", e);
        }
    }

    /**
     * Search for similar vectors in Qdrant
     */
    public List<Map<String, Object>> searchVector(String collectionName, List<Float> vector,
                                                   Integer limit, Map<String, Object> filter) {
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            String url = getBaseUrl() + "/collections/" + collectionName + "/points/search";

            Map<String, Object> body = new HashMap<>();
            body.put("vector", vector);
            body.put("limit", limit != null ? limit : 10);
            body.put("with_payload", true);
            if (filter != null) {
                body.put("filter", filter);
            }

            String jsonBody = objectMapper.writeValueAsString(body);

            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
                    List<Map<String, Object>> searchResults = (List<Map<String, Object>>) responseMap.get("result");
                    if (searchResults != null) {
                        results.addAll(searchResults);
                    }
                } else {
                    log.warn("Search failed: {}", response.message());
                }
            }
        } catch (Exception e) {
            log.error("Error searching vector in Qdrant", e);
        }
        return results;
    }

    /**
     * Delete a point from Qdrant
     */
    public void deletePoint(String collectionName, String pointId) {
        try {
            String url = getBaseUrl() + "/collections/" + collectionName + "/points/" + 
                    Long.parseLong(pointId.hashCode() + "");

            Request request = new Request.Builder()
                    .url(url)
                    .delete()
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.warn("Failed to delete point {} from {}", pointId, collectionName);
                }
            }
        } catch (Exception e) {
            log.error("Error deleting point from Qdrant", e);
        }
    }

    /**
     * Health check for Qdrant
     */
    public boolean healthCheck() {
        try {
            String url = getBaseUrl() + "/health";
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                return response.isSuccessful();
            }
        } catch (Exception e) {
            log.error("Qdrant health check failed", e);
            return false;
        }
    }
}
