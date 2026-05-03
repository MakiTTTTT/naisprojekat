package com.moviex.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class EmbeddingUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Semantic keywords mapped to embedding dimensions
    private static final Map<String, Integer> KEYWORD_DIMENSIONS = new HashMap<>();
    private static final Map<String, Integer> GENRE_DIMENSIONS = new HashMap<>();

    static {
        // Movie attributes (0-50)
        KEYWORD_DIMENSIONS.put("scary", 0);
        KEYWORD_DIMENSIONS.put("horror", 1);
        KEYWORD_DIMENSIONS.put("dark", 2);
        KEYWORD_DIMENSIONS.put("romance", 3);
        KEYWORD_DIMENSIONS.put("romantic", 4);
        KEYWORD_DIMENSIONS.put("love", 5);
        KEYWORD_DIMENSIONS.put("action", 6);
        KEYWORD_DIMENSIONS.put("thriller", 7);
        KEYWORD_DIMENSIONS.put("mystery", 8);
        KEYWORD_DIMENSIONS.put("comedy", 9);
        KEYWORD_DIMENSIONS.put("funny", 10);
        KEYWORD_DIMENSIONS.put("sci-fi", 11);
        KEYWORD_DIMENSIONS.put("scifi", 12);
        KEYWORD_DIMENSIONS.put("science fiction", 13);
        KEYWORD_DIMENSIONS.put("space", 14);
        KEYWORD_DIMENSIONS.put("adventure", 15);
        KEYWORD_DIMENSIONS.put("drama", 16);
        KEYWORD_DIMENSIONS.put("emotional", 17);
        KEYWORD_DIMENSIONS.put("psychological", 18);
        KEYWORD_DIMENSIONS.put("suspense", 19);
        KEYWORD_DIMENSIONS.put("intense", 20);
        KEYWORD_DIMENSIONS.put("breathtaking", 21);
        KEYWORD_DIMENSIONS.put("epic", 22);
        KEYWORD_DIMENSIONS.put("family", 23);
        KEYWORD_DIMENSIONS.put("kids", 24);
        KEYWORD_DIMENSIONS.put("animated", 25);
        KEYWORD_DIMENSIONS.put("animation", 26);
        KEYWORD_DIMENSIONS.put("fantasy", 27);
        KEYWORD_DIMENSIONS.put("magic", 28);
        KEYWORD_DIMENSIONS.put("supernatural", 29);
        KEYWORD_DIMENSIONS.put("crime", 30);
        KEYWORD_DIMENSIONS.put("detective", 31);
        KEYWORD_DIMENSIONS.put("war", 32);
        KEYWORD_DIMENSIONS.put("historical", 33);
        KEYWORD_DIMENSIONS.put("exploration", 34);
        KEYWORD_DIMENSIONS.put("discovery", 35);
        KEYWORD_DIMENSIONS.put("betrayal", 36);
        KEYWORD_DIMENSIONS.put("redemption", 37);
        KEYWORD_DIMENSIONS.put("inspirational", 38);
        KEYWORD_DIMENSIONS.put("heartwarming", 39);
        KEYWORD_DIMENSIONS.put("quirky", 40);
        KEYWORD_DIMENSIONS.put("surreal", 41);
        KEYWORD_DIMENSIONS.put("abstract", 42);
        KEYWORD_DIMENSIONS.put("artistic", 43);
        KEYWORD_DIMENSIONS.put("poignant", 44);
        KEYWORD_DIMENSIONS.put("gripping", 45);
        KEYWORD_DIMENSIONS.put("thrilling", 46);
        KEYWORD_DIMENSIONS.put("haunting", 47);
        KEYWORD_DIMENSIONS.put("atmospheric", 48);
        KEYWORD_DIMENSIONS.put("philosophical", 49);
        KEYWORD_DIMENSIONS.put("suspenseful", 50);

        // Genres (51-68)
        GENRE_DIMENSIONS.put("action", 51);
        GENRE_DIMENSIONS.put("comedy", 52);
        GENRE_DIMENSIONS.put("drama", 53);
        GENRE_DIMENSIONS.put("horror", 54);
        GENRE_DIMENSIONS.put("romance", 55);
        GENRE_DIMENSIONS.put("science fiction", 56);
        GENRE_DIMENSIONS.put("thriller", 57);
        GENRE_DIMENSIONS.put("animation", 58);
        GENRE_DIMENSIONS.put("adventure", 59);
        GENRE_DIMENSIONS.put("historical", 60);
        GENRE_DIMENSIONS.put("crime", 61);
        GENRE_DIMENSIONS.put("fantasy", 62);
        GENRE_DIMENSIONS.put("mystery", 63);
        GENRE_DIMENSIONS.put("documentary", 64);
        GENRE_DIMENSIONS.put("war", 65);
        GENRE_DIMENSIONS.put("western", 66);
        GENRE_DIMENSIONS.put("musical", 67);
        GENRE_DIMENSIONS.put("noir", 68);
    }

    public static float[] textToEmbeddingSimple(String text) {
        float[] embedding = new float[384];

        String lowerText = text.toLowerCase();
        String[] words = lowerText.split("\\s+");

        // Score keywords with higher weights and broader matching
        for (Map.Entry<String, Integer> entry : KEYWORD_DIMENSIONS.entrySet()) {
            String keyword = entry.getKey();
            int dimension = entry.getValue();

            int count = countOccurrences(lowerText, keyword);
            if (count > 0) {
                embedding[dimension] = Math.min(1.0f, count * 0.5f);
            }
        }

        // Score genres with higher weights
        for (Map.Entry<String, Integer> entry : GENRE_DIMENSIONS.entrySet()) {
            String genre = entry.getKey();
            int dimension = entry.getValue();

            int count = countOccurrences(lowerText, genre);
            if (count > 0) {
                embedding[dimension] = Math.min(1.0f, count * 0.8f);
            }
        }

        // Fill in structural features (71-150)
        int wordCount = words.length;
        embedding[71] = Math.min(1.0f, wordCount / 50.0f);
        embedding[72] = Math.min(1.0f, lowerText.length() / 500.0f);
        
        // Add word frequency features for common semantic words
        float[] semanticWeights = new float[20];
        String[] semanticWords = {"epic", "intense", "thrilling", "dark", "beautiful", 
                                  "stunning", "powerful", "emotional", "breathtaking", 
                                  "spectacular", "gripping", "haunting", "amazing", 
                                  "incredible", "dramatic", "compelling", "engaging",
                                  "mysterious", "explosive", "magical"};
        
        for (int i = 0; i < semanticWords.length; i++) {
            int count = countOccurrences(lowerText, semanticWords[i]);
            semanticWeights[i] = Math.min(1.0f, count * 0.3f);
            embedding[150 + i] = semanticWeights[i];
        }

        // Normalize the embedding to improve similarity scores
        float magnitude = 0f;
        for (int i = 0; i < 384; i++) {
            magnitude += embedding[i] * embedding[i];
        }
        magnitude = (float) Math.sqrt(magnitude);
        
        // Only normalize if magnitude is significant
        if (magnitude > 0.5f) {
            for (int i = 0; i < 384; i++) {
                embedding[i] = embedding[i] / magnitude;
            }
        } else if (magnitude > 0) {
            // If very sparse, boost the values
            for (int i = 0; i < 384; i++) {
                embedding[i] = embedding[i] * 2.0f;
            }
        }

        return embedding;
    }

    private static int countOccurrences(String text, String word) {
        int count = 0;
        String[] words = text.split("\\s+|[,\\.!?;:\\-]");
        for (String w : words) {
            if (w.equals(word)) {
                count++;
            }
        }
        return count;
    }

    public static float cosineSimilarity(float[] vec1, float[] vec2) {
        if (vec1.length != vec2.length) {
            return 0f;
        }

        float dotProduct = 0f;
        float magnitude1 = 0f;
        float magnitude2 = 0f;

        for (int i = 0; i < vec1.length; i++) {
            dotProduct += vec1[i] * vec2[i];
            magnitude1 += vec1[i] * vec1[i];
            magnitude2 += vec2[i] * vec2[i];
        }

        magnitude1 = (float) Math.sqrt(magnitude1);
        magnitude2 = (float) Math.sqrt(magnitude2);

        if (magnitude1 == 0f || magnitude2 == 0f) {
            return 0f;
        }

        float similarity = dotProduct / (magnitude1 * magnitude2);
        
        // Boost similarity scores to be more meaningful (cosine similarity is often too strict for sparse vectors)
        // Apply a power function to emphasize stronger matches
        return (float) Math.pow(Math.max(0, similarity), 0.5);
    }
}
