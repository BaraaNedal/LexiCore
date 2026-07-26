package lexicore_Pack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AnalyticsDashboard {

    private int totalTokens;
    private final Set<String> uniqueVocabulary;
    private int totalCharactersExcludingSpaces;
    private final Map<Character, Integer> characterFrequency;

    public AnalyticsDashboard() {
        this.uniqueVocabulary = new HashSet<>();
        this.characterFrequency = new HashMap<>();
    }

    public void computeStats(List<List<String>> sentences, String rawText) {
        reset();

        for (List<String> sentence : sentences) {
            for (String token : sentence) {
                totalTokens++;
                uniqueVocabulary.add(token.toLowerCase());
            }
        }

        if (rawText != null) {
            for (char c : rawText.toCharArray()) {
                if (Character.isWhitespace(c)) {
                    continue;
                }
                totalCharactersExcludingSpaces++;
                characterFrequency.merge(c, 1, Integer::sum);
            }
        }
    }

    public int getTotalTokens() {
        return totalTokens;
    }

    public int getUniqueVocabularyCount() {
        return uniqueVocabulary.size();
    }

    public int getTotalCharactersExcludingSpaces() {
        return totalCharactersExcludingSpaces;
    }

    public Map<Character, Integer> getCharacterFrequency() {
        return Collections.unmodifiableMap(characterFrequency);
    }

    public void reset() {
        totalTokens = 0;
        uniqueVocabulary.clear();
        totalCharactersExcludingSpaces = 0;
        characterFrequency.clear();
    }

    public void display() {
        System.out.println("========== Analytics Dashboard ==========");
        System.out.println("Total tokens (words): " + totalTokens);
        System.out.println("Unique vocabulary (case-insensitive): " + uniqueVocabulary.size());
        System.out.println("Total characters (excluding spaces): " + totalCharactersExcludingSpaces);
        System.out.println("Character frequency:");

        List<Map.Entry<Character, Integer>> sorted = new ArrayList<>(characterFrequency.entrySet());
        sorted.sort((a, b) -> {
            int cmp = b.getValue().compareTo(a.getValue());
            return cmp != 0 ? cmp : a.getKey().compareTo(b.getKey());
        });
        for (Map.Entry<Character, Integer> entry : sorted) {
            System.out.println("  '" + entry.getKey() + "' : " + entry.getValue());
        }
        System.out.println("==========================================");
    }
}
