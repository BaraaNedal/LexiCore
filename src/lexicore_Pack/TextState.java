package lexicore_Pack;

import java.util.ArrayList;
import java.util.List;

/**
 * Immutable snapshot of the document's structural state at one point in time.
 * <p>
 * The engine represents text as a list of sentences, each sentence being a
 * list of word tokens (this matches the "Sentence List Index / Word Token
 * Position" indexing required by the Positional Search Engine feature).
 * A {@code TextState} is a deep copy of that structure, so mutating the live
 * document afterwards can never corrupt a snapshot sitting in the undo/redo
 * stacks.
 */
public class TextState {

    private final List<List<String>> sentences;
    private final String actionDescription;
    private final long timestamp;

    /**
     * @param sentences         the current sentence/token structure to snapshot
     * @param actionDescription short label for what produced this state
     *                          (e.g. "Replace 'old' -> 'new'"), used for feedback
     */
    public TextState(List<List<String>> sentences, String actionDescription) {
        this.sentences = deepCopy(sentences);
        this.actionDescription = actionDescription;
        this.timestamp = System.currentTimeMillis();
    }

    /** Returns a deep copy so the caller can freely mutate it without touching this snapshot. */
    public List<List<String>> getSentences() {
        return deepCopy(sentences);
    }

    public String getActionDescription() {
        return actionDescription;
    }

    public long getTimestamp() {
        return timestamp;
    }

    private static List<List<String>> deepCopy(List<List<String>> source) {
        List<List<String>> copy = new ArrayList<>(source.size());
        for (List<String> sentence : source) {
            copy.add(new ArrayList<>(sentence));
        }
        return copy;
    }

    @Override
    public String toString() {
        return actionDescription + " (" + sentences.size() + " sentences)";
    }
}
