package lexicore_Pack;

import java.util.*;

public class KeywordExtractor {

    private Set<String> stopWords;

    public KeywordExtractor() {
        stopWords = new HashSet<>();
        initializeStopWords();
    }

    private void initializeStopWords() {
        stopWords.add("من"); stopWords.add("إلى"); stopWords.add("في"); stopWords.add("على");
        stopWords.add("عن"); stopWords.add("مع"); stopWords.add("هذا"); stopWords.add("هذه");
        stopWords.add("أن"); stopWords.add("هو"); stopWords.add("هي"); stopWords.add("و");

        stopWords.add("the"); stopWords.add("a"); stopWords.add("an"); stopWords.add("and");
        stopWords.add("is"); stopWords.add("are"); stopWords.add("in"); stopWords.add("on");
        stopWords.add("of"); stopWords.add("to"); stopWords.add("for"); stopWords.add("it");
    }

    public List<String> extractKeywords(String text, int maxKeywords) {
        List<String> keywords = new ArrayList<>();

        if (text == null || text.trim().isEmpty()) {
            return keywords;
        }

        String cleanText = text.toLowerCase().replaceAll("[\\p{Punct}]", "");
        String[] words = cleanText.split("\\s+");

        Map<String, Integer> wordCounts = new HashMap<>();
        for (String word : words) {
            if (word.length() > 2 && !stopWords.contains(word)) {
                wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
            }
        }

        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(wordCounts.entrySet());
        sortedList.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        for (int i = 0; i < Math.min(maxKeywords, sortedList.size()); i++) {
            keywords.add(sortedList.get(i).getKey());
        }

        return keywords;
    }
}