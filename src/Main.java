import lexicore_Pack.AnalyticsDashboard;
import lexicore_Pack.KeywordExtractor;
import lexicore_Pack.PredictiveEngine;
import lexicore_Pack.ReplaceEngine;
import lexicore_Pack.SearchEngine;
import lexicore_Pack.SentimentAnalyzer;
import lexicore_Pack.TextEngine;
import lexicore_Pack.TextState;
import lexicore_Pack.UndoRedoManager;

import java.util.List;
import java.util.Scanner;

/**
 * LexiCore: Mobile Text Processing Engine — console entry point.
 * <p>
 * Wires every feature together behind one continuous menu (Core
 * Requirement #3): text ingestion + pre-processing (TextEngine), the
 * analytics dashboard, positional search, atomic word replacement with
 * undo/redo, next-word prediction, keyword extraction, and sentiment
 * analysis.
 */
public class    Main {

    private static final Scanner scanner = new Scanner(System.in);

    private static final TextEngine textEngine = new TextEngine();
    private static final UndoRedoManager undoRedoManager = new UndoRedoManager();
    private static final PredictiveEngine predictiveEngine = new PredictiveEngine();
    private static final AnalyticsDashboard analyticsDashboard = new AnalyticsDashboard();
    private static final SearchEngine searchEngine = new SearchEngine();
    private static final ReplaceEngine replaceEngine = new ReplaceEngine();
    private static final KeywordExtractor keywordExtractor = new KeywordExtractor();
    private static final SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer();

    public static void main(String[] args) {
        System.out.println("Welcome to LexiCore");
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> loadFromKeyboard();
                case "2" -> loadFromFile();
                case "3" -> showText();
                case "4" -> showDashboard();
                case "5" -> searchText();
                case "6" -> replaceWord();
                case "7" -> undo();
                case "8" -> redo();
                case "9" -> predictNext();
                case "10" -> extractKeywords();
                case "11" -> analyzeSentiment();
                case "0" -> running = false;
                default -> System.out.println("Invalid choice, try again.\n");
            }
        }
        System.out.println("Goodbye!");
    }

    private static void printMenu() {
        System.out.println("================= LexiCore =================");
        System.out.println(" 1. Load text (type/paste)");
        System.out.println(" 2. Load text (file path)");
        System.out.println(" 3. Show current text");
        System.out.println(" 4. Analytics dashboard");
        System.out.println(" 5. Search word/phrase");
        System.out.println(" 6. Replace a word");
        System.out.println(" 7. Undo (" + undoRedoManager.undoCount() + " available)");
        System.out.println(" 8. Redo (" + undoRedoManager.redoCount() + " available)");
        System.out.println(" 9. Predict next word");
        System.out.println("10. Extract keywords");
        System.out.println("11. Analyze sentiment");
        System.out.println(" 0. Exit");
        System.out.print("Choose an option: ");
    }

    private static void loadFromKeyboard() {
        textEngine.loadFromKeyboard(scanner);
        afterLoad();
    }

    private static void loadFromFile() {
        System.out.print("File path: ");
        String path = scanner.nextLine().trim();
        if (!textEngine.loadFromFile(path)) {
            System.out.println("Could not read file: " + path + "\n");
            return;
        }
        afterLoad();
    }

    private static void afterLoad() {
        undoRedoManager.reset();
        retrainDerivedModels();
        System.out.println("Text loaded (" + textEngine.getSentences().size() + " sentence(s)).\n");
    }

    /** Re-trains every model that depends on the current document. Call after any mutation. */
    private static void retrainDerivedModels() {
        List<List<String>> sentences = textEngine.getSentences();
        predictiveEngine.train(sentences);
    }

    private static void showText() {
        if (textEngine.isEmpty()) {
            System.out.println("No text loaded yet.\n");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (List<String> sentence : textEngine.getSentences()) {
            sb.append(String.join(" ", sentence)).append(". ");
        }
        System.out.println(sb.toString().trim() + "\n");
    }

    private static void showDashboard() {
        if (textEngine.isEmpty()) {
            System.out.println("No text loaded yet.\n");
            return;
        }
        analyticsDashboard.computeStats(textEngine.getSentences(), textEngine.getRawText());
        analyticsDashboard.display();
        System.out.println();
    }

    private static void searchText() {
        if (textEngine.isEmpty()) {
            System.out.println("No text loaded yet.\n");
            return;
        }
        System.out.print("Search for: ");
        String query = scanner.nextLine();
        List<SearchEngine.Occurrence> results = searchEngine.search(textEngine.getSentences(), query);
        if (results.isEmpty()) {
            System.out.println("No matches found.\n");
            return;
        }
        System.out.println("Found " + results.size() + " match(es):");
        for (SearchEngine.Occurrence occurrence : results) {
            System.out.println("  " + occurrence);
        }
        System.out.println();
    }

    private static void replaceWord() {
        if (textEngine.isEmpty()) {
            System.out.println("No text loaded yet.\n");
            return;
        }
        System.out.print("Word to replace: ");
        String target = scanner.nextLine().trim();
        System.out.print("Replace with: ");
        String replacement = scanner.nextLine().trim();

        List<List<String>> sentences = textEngine.getSentences();
        undoRedoManager.saveState(sentences, "Replace '" + target + "' -> '" + replacement + "'");

        ReplaceEngine.ReplaceResult result = replaceEngine.replaceAll(sentences, target, replacement);
        textEngine.setSentences(sentences);
        retrainDerivedModels();

        System.out.println(result + "\n");
    }

    private static void undo() {
        TextState restored = undoRedoManager.undo(textEngine.getSentences());
        if (restored == null) {
            System.out.println("Nothing to undo.\n");
            return;
        }
        textEngine.setSentences(restored.getSentences());
        retrainDerivedModels();
        System.out.println("Undone: " + restored.getActionDescription() + "\n");
    }

    private static void redo() {
        TextState reApplied = undoRedoManager.redo(textEngine.getSentences());
        if (reApplied == null) {
            System.out.println("Nothing to redo.\n");
            return;
        }
        textEngine.setSentences(reApplied.getSentences());
        retrainDerivedModels();
        System.out.println("Redone: " + reApplied.getActionDescription() + "\n");
    }

    private static void predictNext() {
        if (textEngine.isEmpty()) {
            System.out.println("No text loaded yet.\n");
            return;
        }
        System.out.print("Current word: ");
        String word = scanner.nextLine().trim();
        List<String> suggestions = predictiveEngine.predictTopN(word, 3);
        if (suggestions.isEmpty()) {
            System.out.println("No prediction available for '" + word + "'.\n");
        } else {
            System.out.println("Suggestions: " + suggestions + "\n");
        }
    }

    private static void extractKeywords() {
        if (textEngine.isEmpty()) {
            System.out.println("No text loaded yet.\n");
            return;
        }
        List<String> keywords = keywordExtractor.extractKeywords(textEngine.getRawText(), 5);
        System.out.println(keywords.isEmpty() ? "No keywords found." : "Top keywords: " + keywords);
        System.out.println();
    }

    private static void analyzeSentiment() {
        if (textEngine.isEmpty()) {
            System.out.println("No text loaded yet.\n");
            return;
        }
        System.out.println("Sentiment: " + sentimentAnalyzer.analyzeSentiment(textEngine.getRawText()));
        System.out.println();
    }

}
