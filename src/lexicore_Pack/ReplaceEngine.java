package lexicore_Pack;

import java.util.List;

/**
 * Atomic Word Replacement with State Tracking.
 * <p>
 * Replaces every occurrence of a target word across the whole document in
 * one atomic pass, and reports performance feedback (mutation count and
 * elapsed processing time). This class only performs the mutation itself —
 * callers are expected to call {@link UndoRedoManager#saveState} with the
 * pre-mutation state first, so the change can be rolled back later.
 */
public class ReplaceEngine {

    /** Outcome of one replace-all operation: how much changed, and how fast. */
    public static class ReplaceResult {
        private final int mutationCount;
        private final long elapsedMillis;

        public ReplaceResult(int mutationCount, long elapsedMillis) {
            this.mutationCount = mutationCount;
            this.elapsedMillis = elapsedMillis;
        }

        public int getMutationCount() {
            return mutationCount;
        }

        public long getElapsedMillis() {
            return elapsedMillis;
        }

        @Override
        public String toString() {
            return mutationCount + " replacement(s) in " + elapsedMillis + " ms";
        }
    }

    /**
     * Replaces every case-insensitive match of {@code target} with
     * {@code replacement} across every sentence, mutating the given
     * structure in place.
     */
    public ReplaceResult replaceAll(List<List<String>> sentences, String target, String replacement) {
        long startNanos = System.nanoTime();
        int mutationCount = 0;

        if (target != null && !target.isBlank() && replacement != null) {
            String normalizedTarget = target.trim().toLowerCase();
            String normalizedReplacement = replacement.trim().toLowerCase();

            for (List<String> sentence : sentences) {
                for (int i = 0; i < sentence.size(); i++) {
                    if (sentence.get(i).equalsIgnoreCase(normalizedTarget)) {
                        sentence.set(i, normalizedReplacement);
                        mutationCount++;
                    }
                }
            }
        }

        long elapsedMillis = (System.nanoTime() - startNanos) / 1_000_000;
        return new ReplaceResult(mutationCount, elapsedMillis);
    }
}
