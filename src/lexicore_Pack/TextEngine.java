package lexicore_Pack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class TextEngine {

    /** Typed on its own line to end a direct keyboard input stream. */
    public static final String END_SENTINEL = "$$END_TEXT$$";

    private String rawText = "";
    private List<List<String>> sentences = new ArrayList<>();

    /**
     * Reads lines from {@code scanner} until the user types
     * {@link #END_SENTINEL} on its own line, then pre-processes the result.
     */
    public void loadFromKeyboard(Scanner scanner) {
        StringBuilder builder = new StringBuilder();
        System.out.println("Type or paste your text. Enter " + END_SENTINEL + " on its own line to finish:");
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.trim().equals(END_SENTINEL)) {
                break;
            }
            builder.append(line).append("\n");
        }
        setText(builder.toString());
    }

    /**
     * Reads the full contents of the file at {@code path} and pre-processes it.
     *
     * @return true on success, false if the file could not be read
     */
    public boolean loadFromFile(String path) {
        try {
            String content = Files.readString(Path.of(path));
            setText(content);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /** Directly sets and pre-processes a block of text (also used by tests/menus). */
    public void setText(String text) {
        this.rawText = text == null ? "" : text;
        this.sentences = preprocess(this.rawText);
    }

    public String getRawText() {
        return rawText;
    }

    /** Deep copy of the current sentence/token structure. */
    public List<List<String>> getSentences() {
        List<List<String>> copy = new ArrayList<>(sentences.size());
        for (List<String> sentence : sentences) {
            copy.add(new ArrayList<>(sentence));
        }
        return copy;
    }

    /** Replaces the live sentence/token structure (e.g. after an undo/redo). */
    public void setSentences(List<List<String>> newSentences) {
        this.sentences = new ArrayList<>();
        for (List<String> sentence : newSentences) {
            this.sentences.add(new ArrayList<>(sentence));
        }
    }

    public boolean isEmpty() {
        return sentences.isEmpty();
    }

    public void reset() {
        rawText = "";
        sentences = new ArrayList<>();
    }

    /**
     * Mobile-optimized pre-processing pipeline: collapses whitespace,
     * splits into sentences on '.', '!', '?', lowercases everything, and
     * strips standard punctuation from each token.
     */
    private List<List<String>> preprocess(String text) {
        List<List<String>> result = new ArrayList<>();
        String normalized = text.replaceAll("\\s+", " ").trim();
        if (normalized.isEmpty()) {
            return result;
        }

        String[] rawSentences = normalized.split("(?<=[.!?])\\s+");
        for (String rawSentence : rawSentences) {
            String lower = rawSentence.trim().toLowerCase();
            String cleaned = lower.replaceAll("[\\p{Punct}]", "");
            List<String> tokens = new ArrayList<>();
            for (String word : cleaned.split("\\s+")) {
                if (!word.isEmpty()) {
                    tokens.add(word);
                }
            }
            if (!tokens.isEmpty()) {
                result.add(tokens);
            }
        }
        return result;
    }
}
