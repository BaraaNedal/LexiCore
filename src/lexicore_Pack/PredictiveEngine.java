package lexicore_Pack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PredictiveEngine {

  
    private final Map<String, Map<String, Integer>> bigramModel;

    public PredictiveEngine() {
        this.bigramModel = new HashMap<>();
    }


    public void train(List<List<String>> sentences) {
        bigramModel.clear();
        for (List<String> sentence : sentences) {
            for (int i = 0; i < sentence.size() - 1; i++) {
                addBigram(sentence.get(i), sentence.get(i + 1));
            }
        }
    }

 
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


    public String predictNext(String currentWord) {
        List<String> top = predictTopN(currentWord, 1);
        return top.isEmpty() ? null : top.get(0);
    }

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

    public boolean hasPrediction(String currentWord) {
        return currentWord != null && bigramModel.containsKey(currentWord.toLowerCase());
    }

    public int vocabularySize() {
        return bigramModel.size();
    }

    public void reset() {
        bigramModel.clear();
    }
}
