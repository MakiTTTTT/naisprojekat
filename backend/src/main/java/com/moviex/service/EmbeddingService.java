package com.moviex.service;

import com.moviex.util.EmbeddingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class EmbeddingService {

    public List<Float> generateEmbedding(String text) {
        try {
            float[] embedding = EmbeddingUtil.textToEmbeddingSimple(text);
            List<Float> result = new ArrayList<>();
            for (float value : embedding) {
                result.add(value);
            }
            return result;
        } catch (Exception e) {
            log.error("Error generating embedding for text: {}", text, e);
            // Return a default embedding on error
            return getDefaultEmbedding();
        }
    }

    public float calculateSimilarity(List<Float> vec1, List<Float> vec2) {
        if (vec1.size() != vec2.size()) {
            return 0f;
        }

        float[] array1 = new float[vec1.size()];
        float[] array2 = new float[vec2.size()];

        for (int i = 0; i < vec1.size(); i++) {
            array1[i] = vec1.get(i);
            array2[i] = vec2.get(i);
        }

        return EmbeddingUtil.cosineSimilarity(array1, array2);
    }

    private List<Float> getDefaultEmbedding() {
        List<Float> embedding = new ArrayList<>();
        for (int i = 0; i < 384; i++) {
            embedding.add(0f);
        }
        return embedding;
    }
}
