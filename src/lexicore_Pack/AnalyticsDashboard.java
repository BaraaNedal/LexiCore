package lexicore_Pack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Local Analytics Dashboard.
 * <p>
 * Aggregates lightweight, on-device statistics about the loaded text:
 * total token count, unique (case-insensitive) vocabulary size, total
 * character count (excluding spaces), and a per-character frequency map.
 */
public class AnalyticsDashboard {

    private int totalTokens;
    private final Set<String> uniqueVocabulary;
    private int totalCharactersExcludingSpaces;
    private final Map<Character, Integer> characterFrequency;

    public AnalyticsDashboard() {
        this.uniqueVocabulary = new HashSet<>();
        this.characterFrequency = new HashMap<>();
    }

    /**
     * Recomputes every statistic from scratch.
     *
     * @param sentences tokenized corpus (list of sentences, each a list of
     *                  word tokens) — used for the token count and vocabulary
     * @param rawText   the original loaded text — used for character-level
     *                  statistics, since tokenizing/lowercasing would lose
     *                  the exact character makeup of what the user typed
     */
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

    /** Unmodifiable view of the character -> frequency map. */
    public Map<Character, Integer> getCharacterFrequency() {
        return Collections.unmodifiableMap(characterFrequency);
    }

    public void reset() {
        totalTokens = 0;
        uniqueVocabulary.clear();
        totalCharactersExcludingSpaces = 0;
        characterFrequency.clear();
    }

    /** Prints the consolidated dashboard view to the console. */
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
