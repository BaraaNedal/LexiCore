package lexicore_Pack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Next-Word Predictive Engine.
 * <p>
 * Learns local bigram (word -&gt; next word) transitions from the loaded
 * corpus using nested {@link HashMap}s ({@code HashMap<String, HashMap<String,
 * Integer>>}), then suggests the most probable next word(s) for a given
 * current word — mimicking a mobile keyboard's next-word prediction.
 */
public class PredictiveEngine {

    // currentWord -> (nextWord -> observed frequency)
    private final Map<String, Map<String, Integer>> bigramModel;

    public PredictiveEngine() {
        this.bigramModel = new HashMap<>();
    }

    /**
     * Builds/rebuilds the bigram model from the given sentence/token
     * structure. Bigrams are only counted within a sentence — a sentence's
     * last word is never linked to the next sentence's first word.
     */
    public void train(List<List<String>> sentences) {
        bigramModel.clear();
        for (List<String> sentence : sentences) {
            for (int i = 0; i < sentence.size() - 1; i++) {
                addBigram(sentence.get(i), sentence.get(i + 1));
            }
        }
    }

    /**
     * Incrementally records one more observed transition {@code currentWord
     * -> nextWord}. Useful for updating the model live as new text streams
     * in, without a full retrain.
     */
    public void addBigram(String currentWord, String nextWord) {
        if (currentWord == null || nextWord == null) {
            return;
        }
        String key = currentWord.toLowerCase();
        String value = nextWord.toLowerCase();
        bigramModel
                .computeIfAbsent(key, k -> new HashMap<>())
                .merge(value, 1, Integer::sum);
    }

    /** Returns the single most probable next word, or {@code null} if unseen. */
    public String predictNext(String currentWord) {
        List<String> top = predictTopN(currentWord, 1);
        return top.isEmpty() ? null : top.get(0);
    }

    /**
     * Returns up to {@code n} candidate next words for {@code currentWord},
     * ranked by descending observed frequency (ties broken alphabetically
     * for deterministic, testable output).
     */
    public List<String> predictTopN(String currentWord, int n) {
        if (currentWord == null) {
            return Collections.emptyList();
        }
        Map<String, Integer> candidates = bigramModel.get(currentWord.toLowerCase());
        if (candidates == null || candidates.isEmpty()) {
            return Collections.emptyList();
        }

        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(candidates.entrySet());
        sorted.sort((a, b) -> {
            int cmp = b.getValue().compareTo(a.getValue());
            return cmp != 0 ? cmp : a.getKey().compareTo(b.getKey());
        });

        List<String> result = new ArrayList<>();
        for (int i = 0; i < Math.min(n, sorted.size()); i++) {
            result.add(sorted.get(i).getKey());
        }
        return result;
    }

    /** True if the model has ever observed {@code currentWord} starting a bigram. */
    public boolean hasPrediction(String currentWord) {
        return currentWord != null && bigramModel.containsKey(currentWord.toLowerCase());
    }

    /** Number of distinct "current words" the model has transitions for. */
    public int vocabularySize() {
        return bigramModel.size();
    }

    public void reset() {
        bigramModel.clear();
    }
}
