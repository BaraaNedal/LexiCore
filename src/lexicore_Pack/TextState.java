package lexicore_Pack;

import java.util.ArrayList;
import java.util.List;


public class TextState {

    private final List<List<String>> sentences;
    private final String actionDescription;
    private final long timestamp;


    public TextState(List<List<String>> sentences, String actionDescription) {
        this.sentences = deepCopy(sentences);
        this.actionDescription = actionDescription;
        this.timestamp = System.currentTimeMillis();
    }

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
