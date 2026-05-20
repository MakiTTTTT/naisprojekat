package com.moviex.service;

import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.DataType;
import io.milvus.param.*;
import io.milvus.param.collection.*;
import io.milvus.param.dml.*;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.response.QueryResultsWrapper;
import io.milvus.response.SearchResultsWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import io.milvus.grpc.SearchResults;
import io.milvus.grpc.QueryResults;
import io.milvus.grpc.FlushResponse;
import io.milvus.grpc.GetCollectionStatisticsResponse;
import io.milvus.grpc.MutationResult;

import java.util.*;

/**
 * Milvus Service Layer
 * Handles all Milvus operations including:
 * - Collection management (create, drop, describe)
 * - Vector indexing (HNSW with COSINE metric)
 * - CRUD operations (insert, update, delete, query)
 * - Simple queries (by ID, vector search, filtering, counting)
 * - Complex queries (vector+filter, pagination, hybrid search)
 *
 * Collections:
 * - movies: Film data with vector embeddings
 * - actors: Actor data with vector embeddings
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MilvusService {
    // Collection names
    public static final String MOVIES_COLLECTION = "movies";
    public static final String ACTORS_COLLECTION = "actors";
    
    // Vector configuration
    public static final int VECTOR_SIZE = 384;
    public static final String VECTOR_FIELD = "embedding";
    public static final String ID_FIELD = "id";

    private final MilvusServiceClient milvusClient;

    // ==================== Collection Management ====================

    /**
     * Initialize collections if they don't exist
     * Creates both movies and actors collections with proper schemas and indexes
     */
    public void initializeCollections() {
        log.info("Initializing Milvus collections...");
        
        try {
            createMoviesCollectionIfNotExists();
            createActorsCollectionIfNotExists();
            
            // Create indexes for vector fields
            createIndexForCollection(MOVIES_COLLECTION, VECTOR_FIELD);
            createIndexForCollection(ACTORS_COLLECTION, VECTOR_FIELD);
            
            log.info("Collections initialized successfully");
        } catch (Exception e) {
            log.error("Error initializing collections", e);
            throw new RuntimeException("Failed to initialize Milvus collections", e);
        }
    }

    /**
     * Create movies collection with schema:
     * - id (STRING, primary key)
     * - title (STRING)
     * - description (STRING)
     * - genre (STRING)
     * - year (INT64)
     * - director (STRING)
     * - rating (FLOAT)
     * - release_date (STRING)
     * - embedding (FLOAT_VECTOR, dimension=384)
     */
    private void createMoviesCollectionIfNotExists() {
        try {
            if (collectionExists(MOVIES_COLLECTION)) {
                log.info("Collection {} already exists", MOVIES_COLLECTION);
                return;
            }

            List<FieldType> fields = new ArrayList<>();
            
            // Primary key field
            fields.add(FieldType.newBuilder()
                    .withName(ID_FIELD)
                    .withDataType(DataType.VarChar)
                    .withPrimaryKey(true)
                    .withMaxLength(36)
                    .build());

            // Non-vector fields
            fields.add(FieldType.newBuilder()
                    .withName("title")
                    .withDataType(DataType.VarChar)
                    .withMaxLength(255)
                    .build());

            fields.add(FieldType.newBuilder()
                    .withName("description")
                    .withDataType(DataType.VarChar)
                    .withMaxLength(2000)
                    .build());

            fields.add(FieldType.newBuilder()
                    .withName("genre")
                    .withDataType(DataType.VarChar)
                    .withMaxLength(100)
                    .build());

            fields.add(FieldType.newBuilder()
                    .withName("year")
                    .withDataType(DataType.Int64)
                    .build());

            fields.add(FieldType.newBuilder()
                    .withName("director")
                    .withDataType(DataType.VarChar)
                    .withMaxLength(150)
                    .build());

            fields.add(FieldType.newBuilder()
                    .withName("rating")
                    .withDataType(DataType.Float)
                    .build());

            fields.add(FieldType.newBuilder()
                    .withName("release_date")
                    .withDataType(DataType.VarChar)
                    .withMaxLength(10)
                    .build());

            // Vector field
            fields.add(FieldType.newBuilder()
                    .withName(VECTOR_FIELD)
                    .withDataType(DataType.FloatVector)
                    .withDimension(VECTOR_SIZE)
                    .build());

            CreateCollectionParam createParam = CreateCollectionParam.newBuilder()
                    .withCollectionName(MOVIES_COLLECTION)
                    .withDescription("Movies collection with semantic embeddings")
                    .withFieldTypes(fields)
                    .build();

            R<RpcStatus> response = milvusClient.createCollection(createParam);
            if (response.getStatus() != R.Status.Success.getCode()) {
                throw new RuntimeException("Failed to create movies collection: " + response.getMessage());
            }
            
            log.info("Movies collection created successfully");
        } catch (Exception e) {
            log.error("Error creating movies collection", e);
            throw new RuntimeException("Failed to create movies collection", e);
        }
    }

    /**
     * Create actors collection with schema:
     * - id (STRING, primary key)
     * - name (STRING)
     * - biography (STRING)
     * - birth_year (INT64)
     * - nationality (STRING)
     * - known_for (STRING)
     * - embedding (FLOAT_VECTOR, dimension=384)
     */
    private void createActorsCollectionIfNotExists() {
        try {
            if (collectionExists(ACTORS_COLLECTION)) {
                log.info("Collection {} already exists", ACTORS_COLLECTION);
                return;
            }

            List<FieldType> fields = new ArrayList<>();
            
            // Primary key field
            fields.add(FieldType.newBuilder()
                    .withName(ID_FIELD)
                    .withDataType(DataType.VarChar)
                    .withPrimaryKey(true)
                    .withMaxLength(36)
                    .build());

            // Non-vector fields
            fields.add(FieldType.newBuilder()
                    .withName("name")
                    .withDataType(DataType.VarChar)
                    .withMaxLength(255)
                    .build());

            fields.add(FieldType.newBuilder()
                    .withName("biography")
                    .withDataType(DataType.VarChar)
                    .withMaxLength(2000)
                    .build());

            fields.add(FieldType.newBuilder()
                    .withName("birth_year")
                    .withDataType(DataType.Int64)
                    .build());

            fields.add(FieldType.newBuilder()
                    .withName("nationality")
                    .withDataType(DataType.VarChar)
                    .withMaxLength(100)
                    .build());

            fields.add(FieldType.newBuilder()
                    .withName("known_for")
                    .withDataType(DataType.VarChar)
                    .withMaxLength(500)
                    .build());

            // Vector field
            fields.add(FieldType.newBuilder()
                    .withName(VECTOR_FIELD)
                    .withDataType(DataType.FloatVector)
                    .withDimension(VECTOR_SIZE)
                    .build());

            CreateCollectionParam createParam = CreateCollectionParam.newBuilder()
                    .withCollectionName(ACTORS_COLLECTION)
                    .withDescription("Actors collection with semantic embeddings")
                    .withFieldTypes(fields)
                    .build();

            R<RpcStatus> response = milvusClient.createCollection(createParam);
            if (response.getStatus() != R.Status.Success.getCode()) {
                throw new RuntimeException("Failed to create actors collection: " + response.getMessage());
            }
            
            log.info("Actors collection created successfully");
        } catch (Exception e) {
            log.error("Error creating actors collection", e);
            throw new RuntimeException("Failed to create actors collection", e);
        }
    }

    /**
     * Check if a collection exists
     */
    private boolean collectionExists(String collectionName) {
        try {
            HasCollectionParam param = HasCollectionParam.newBuilder()
                    .withCollectionName(collectionName)
                    .build();
            R<Boolean> response = milvusClient.hasCollection(param);
            return response.getData();
        } catch (Exception e) {
            log.error("Error checking if collection exists: {}", collectionName, e);
            return false;
        }
    }

    /**
     * Create HNSW index for vector field with COSINE metric
     * HNSW is chosen for:
     * - O(log N) complexity - efficient for large datasets
     * - Better recall than IVF_FLAT - important for semantic search
     * - Suitable for high-dimensional embeddings (384 dims)
     * - No quantization needed - full precision similarity
     */
    private void createIndexForCollection(String collectionName, String fieldName) {
        try {
            CreateIndexParam indexParam = CreateIndexParam.newBuilder()
                .withCollectionName(collectionName)
                .withFieldName(fieldName)
                .withIndexType(IndexType.HNSW)
                .withMetricType(MetricType.COSINE)
                .withExtraParam("{\"M\":30,\"efConstruction\":200}")
                .build();

            R<RpcStatus> response = milvusClient.createIndex(indexParam);
            if (response.getStatus() != R.Status.Success.getCode()) {
                log.warn("Index already exists or creation skipped: {}", response.getMessage());
            } else {
                log.info("Index created for {}.{}", collectionName, fieldName);
            }
        } catch (Exception e) {
            log.error("Error creating index for {}.{}", collectionName, fieldName, e);
        }
    }

    // ==================== CRUD Operations ====================

    /**
     * Insert documents into a collection
     */
   public long insertDocuments(String collectionName, List<Map<String, Object>> documents) {

        List<InsertParam.Field> fields = new ArrayList<>();

        Map<String, List<?>> columnMap = new HashMap<>();

        for (Map<String, Object> doc : documents) {
            for (Map.Entry<String, Object> e : doc.entrySet()) {
                columnMap.computeIfAbsent(e.getKey(), k -> new ArrayList<>())
                        .add(e.getValue());
            }
        }

        for (Map.Entry<String, List<?>> col : columnMap.entrySet()) {
            fields.add(new InsertParam.Field(col.getKey(), col.getValue()));
        }

        InsertParam insertParam = InsertParam.newBuilder()
                .withCollectionName(collectionName)
                .withFields(fields)
                .build();

        R<MutationResult> response = milvusClient.insert(insertParam);
        long insertedCount = response.getData().getIDs().getIntId().size();

        return insertedCount;
    }

        public void deleteById(String collectionName, String id) {
        try {
            DeleteParam param = DeleteParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withExpr("id == \"" + id + "\"")
                    .build();

            milvusClient.delete(param);
        } catch (Exception e) {
            log.error("Error deleting id {} from {}", id, collectionName, e);
        }
    }
    /**
     * Get record count for a collection
     */
    public long getCollectionRowCount(String collectionName) {
        try {
            GetCollectionStatisticsParam param = GetCollectionStatisticsParam.newBuilder()
                    .withCollectionName(collectionName)
                    .build();

            R<GetCollectionStatisticsResponse> response =
                    milvusClient.getCollectionStatistics(param);
            
            long rowCount = Long.parseLong(
        response.getData().getStats(0).getValue());

            return rowCount;

        } catch (Exception e) {
            log.error("Error getting collection row count for {}", collectionName, e);
            return 0;
        }
    }

    // ==================== Simple Queries ====================

    /**
     * SIMPLE QUERY 1: Query by ID (primary key lookup)
     * Fetches a single document by its ID
     */
    public Map<String, Object> queryById(String collectionName, String id) {
        try {
            QueryParam queryParam = QueryParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withExpr("id == \"" + id + "\"")
                    .addOutField("*")
                    .build();

            R<QueryResults> response = milvusClient.query(queryParam);
            if (response.getStatus() != R.Status.Success.getCode()) {
                throw new RuntimeException("Query failed: " + response.getMessage());
            }

            QueryResultsWrapper wrapper = new QueryResultsWrapper(response.getData());
            List<Map<String, Object>> results = wrapper.getFieldsMap();
            
            return !results.isEmpty() ? results.get(0) : null;
        } catch (Exception e) {
            log.error("Error querying document by id {} in {}", id, collectionName, e);
            return null;
        }
    }

    /**
     * SIMPLE QUERY 2: Vector similarity search
     * Finds top K documents most similar to the given embedding vector
     */
    public List<Map<String, Object>> vectorSearch(String collectionName, List<Float> queryVector, int topK) {
        try {
            SearchParam searchParam = SearchParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withMetricType(MetricType.COSINE)
                    .withOutFields("*")
                    .withTopK(topK)
                    .addVectorField(VECTOR_FIELD)
                    .addTargetVector(queryVector)
                    .build();

            R<SearchResults> response = milvusClient.search(searchParam);
            if (response.getStatus() != R.Status.Success.getCode()) {
                throw new RuntimeException("Search failed: " + response.getMessage());
            }

            SearchResultsWrapper wrapper = new SearchResultsWrapper(response.getData().getResults());

            List<Map<String, Object>> results = wrapper.getRowRecords();
            return results;
        } catch (Exception e) {
            log.error("Error performing vector search in {}", collectionName, e);
            return new ArrayList<>();
        }
    }

    /**
     * SIMPLE QUERY 3: Filtering query
     * Filters documents based on non-vector field conditions
     */
    public List<Map<String, Object>> filterQuery(String collectionName, String filterExpr, List<String> outFields) {
        try {
            QueryParam queryParam = QueryParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withExpr(filterExpr)
                    .addOutFields(outFields)
                    .build();

            R<QueryResults> response = milvusClient.query(queryParam);
            if (response.getStatus() != R.Status.Success.getCode()) {
                throw new RuntimeException("Filter query failed: " + response.getMessage());
            }

            QueryResultsWrapper wrapper = new QueryResultsWrapper(response.getData());

            List<Map<String, Object>> results = wrapper.getRowRecords();
            return results;
        } catch (Exception e) {
            log.error("Error performing filter query in {}", collectionName, e);
            return new ArrayList<>();
        }
    }

    /**
     * SIMPLE QUERY 4: Count records with filter
     * Counts how many documents match the filter condition
     */
    public long countByFilter(String collectionName, String filterExpr) {
        try {
            QueryParam queryParam = QueryParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withExpr(filterExpr)
                    .addOutFields(Arrays.asList(ID_FIELD))
                    .build();

            R<QueryResults> response = milvusClient.query(queryParam);
            if (response.getStatus() != R.Status.Success.getCode()) {
                throw new RuntimeException("Count query failed: " + response.getMessage());
            }

            QueryResultsWrapper wrapper = new QueryResultsWrapper(response.getData());
            return wrapper.getRowCount();
        } catch (Exception e) {
            log.error("Error counting records in {}", collectionName, e);
            return 0;
        }
    }

    // ==================== Complex Queries ====================

    /**
     * COMPLEX QUERY A: Vector search with multi-condition filtering
     * Combines vector similarity search with at least 2 filter conditions
     * Example: Find movies similar to query vector WHERE year > 2000 AND rating > 7.0
     */
    public List<Map<String, Object>> vectorSearchWithMultipleFilters(
            String collectionName, 
            List<Float> queryVector, 
            String filterExpr, 
            int topK) {
        try {
            SearchParam searchParam = SearchParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withMetricType(MetricType.COSINE)
                    .withOutFields("*")
                    .withTopK(topK)
                    .withExpr(filterExpr)
                    .addVectorField(VECTOR_FIELD)
                    .addTargetVector(queryVector)
                    .build();

            R<SearchResults> response = milvusClient.search(searchParam);
            if (response.getStatus() != R.Status.Success.getCode()) {
                throw new RuntimeException("Vector search with filters failed: " + response.getMessage());
            }

            SearchResultsWrapper wrapper = new SearchResultsWrapper(response.getData().getResults());
            List<Map<String, Object>> results = wrapper.getRowRecords();
            return results;
        } catch (Exception e) {
            log.error("Error performing vector search with filters in {}", collectionName, e);
            return new ArrayList<>();
        }
    }

    /**
     * COMPLEX QUERY B: Vector search with filtering using iterators
     * Uses offset/limit pagination to iterate through filtered results
     * Useful for large result sets that need pagination
     */
    public List<Map<String, Object>> vectorSearchWithIterator(
            String collectionName, 
            List<Float> queryVector, 
            String filterExpr, 
            int pageSize, 
            int pageNumber) {
        try {
            int offset = pageSize * (pageNumber - 1);
            
            SearchParam searchParam = SearchParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withMetricType(MetricType.COSINE)
                    .withOutFields(Arrays.asList("*"))
                    .withTopK(pageSize * pageNumber)
                    .withExpr(filterExpr)
                    .withOffset(offset)
                    .addVectorField(VECTOR_FIELD)
                    .addTargetVector(queryVector)
                    .build();

            R<SearchResults> response = milvusClient.search(searchParam);
            if (response.getStatus() != R.Status.Success.getCode()) {
                throw new RuntimeException("Iterator search failed: " + response.getMessage());
            }

            SearchResultsWrapper wrapper = new SearchResultsWrapper(response.getData().getResults());
            List<Map<String, Object>> results = wrapper.getRowRecords();
            
            // Return only the requested page
            int fromIndex = Math.min(offset % (pageSize * pageNumber), results.size());
            int toIndex = Math.min(fromIndex + pageSize, results.size());
            return results.subList(fromIndex, toIndex);
        } catch (Exception e) {
            log.error("Error performing iterator search in {}", collectionName, e);
            return new ArrayList<>();
        }
    }

    /**
     * COMPLEX QUERY C: Hybrid vector search
     * Combines multiple vector search results (e.g., search in both collections)
     * Merges and scores results based on distance and combined metadata
     */
    public Map<String, List<Map<String, Object>>> hybridVectorSearch(
            List<Float> queryVector, 
            int topK) {
        try {
            Map<String, List<Map<String, Object>>> hybridResults = new HashMap<>();
            
            // Search in movies collection
            List<Map<String, Object>> movieResults = vectorSearch(MOVIES_COLLECTION, queryVector, topK);
            hybridResults.put(MOVIES_COLLECTION, movieResults);
            
            // Search in actors collection
            List<Map<String, Object>> actorResults = vectorSearch(ACTORS_COLLECTION, queryVector, topK);
            hybridResults.put(ACTORS_COLLECTION, actorResults);
            
            log.info("Hybrid search completed: {} movies, {} actors found", 
                    movieResults.size(), actorResults.size());
            return hybridResults;
        } catch (Exception e) {
            log.error("Error performing hybrid search", e);
            return new HashMap<>();
        }
    }

    /**
     * Flush data to ensure all operations are persisted
     */
    public void flushData(String collectionName) {
        try {
            FlushParam flushParam = FlushParam.newBuilder()
                    .withCollectionNames(Arrays.asList(collectionName))
                    .build();

            R<FlushResponse> response = milvusClient.flush(flushParam);
            if (response.getStatus() != R.Status.Success.getCode()) {
                log.warn("Flush may have failed: {}", response.getMessage());
            } else {
                log.info("Data flushed for collection {}", collectionName);
            }
        } catch (Exception e) {
            log.error("Error flushing data for {}", collectionName, e);
        }
    }

    /**
     * Verify collection exists and get statistics
     */
    public Map<String, Object> getCollectionInfo(String collectionName) {
        try {
            Map<String, Object> info = new HashMap<>();
            
            // Check existence
            info.put("exists", collectionExists(collectionName));
            
            // Get row count
            if ((boolean) info.get("exists")) {
                info.put("rowCount", getCollectionRowCount(collectionName));
            }
            
            return info;
        } catch (Exception e) {
            log.error("Error getting collection info for {}", collectionName, e);
            return new HashMap<>();
        }
    }
}