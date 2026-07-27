package lexicore_Pack;

import java.util.ArrayList;
import java.util.List;

/**
 * Positional Search Engine.
 * <p>
 * Locates every exact occurrence of a word or phrase within the loaded
 * document and reports its structural position — sentence list index and
 * word token position within that sentence — using a small structural
 * wrapper ({@link Occurrence}) instead of raw indices.
 */
public class SearchEngine {

    /** One exact match location: which sentence, and which word inside it. */
    public static class Occurrence {
        private final int sentenceIndex;
        private final int tokenPosition;

        public Occurrence(int sentenceIndex, int tokenPosition) {
            this.sentenceIndex = sentenceIndex;
            this.tokenPosition = tokenPosition;
        }

        public int getSentenceIndex() {
            return sentenceIndex;
        }

        public int getTokenPosition() {
            return tokenPosition;
        }

        @Override
        public String toString() {
            return "Sentence " + sentenceIndex + ", Word " + tokenPosition;
        }
    }

    /**
     * Searches for {@code query} (a single word or a multi-word phrase)
     * within {@code sentences}, matching case-insensitively.
     *
     * @return every occurrence found, in document order
     */
    public List<Occurrence> search(List<List<String>> sentences, String query) {
        List<Occurrence> results = new ArrayList<>();
        if (query == null || query.isBlank()) {
            return results;
        }

        String[] queryWords = query.trim().toLowerCase().split("\\s+");

        for (int s = 0; s < sentences.size(); s++) {
            List<String> sentence = sentences.get(s);
            for (int t = 0; t <= sentence.size() - queryWords.length; t++) {
                if (matchesAt(sentence, t, queryWords)) {
                    results.add(new Occurrence(s, t));
                }
            }
        }
        return results;
    }

    private boolean matchesAt(List<String> sentence, int start, String[] queryWords) {
        for (int i = 0; i < queryWords.length; i++) {
            if (!sentence.get(start + i).equalsIgnoreCase(queryWords[i])) {
                return false;
            }
        }
        return true;
    }
}
