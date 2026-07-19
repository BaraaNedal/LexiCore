package lexicore_Pack;

import java.util.HashSet;
import java.util.Set;

public class SentimentAnalyzer {

    private Set<String> positiveWords;
    private Set<String> negativeWords;

    public SentimentAnalyzer() {
        positiveWords = new HashSet<>();
        negativeWords = new HashSet<>();
        initializeLexicon();
    }

    private void initializeLexicon() {
        positiveWords.add("ممتاز"); positiveWords.add("رائع"); positiveWords.add("جميل");
        positiveWords.add("ناجح"); positiveWords.add("ذكي"); positiveWords.add("مفيد");
        positiveWords.add("good"); positiveWords.add("great"); positiveWords.add("excellent");
        positiveWords.add("happy"); positiveWords.add("awesome"); positiveWords.add("love");

        negativeWords.add("سيء"); negativeWords.add("رديء"); negativeWords.add("فاشل");
        negativeWords.add("خطأ"); negativeWords.add("صعب"); negativeWords.add("حزين");
        negativeWords.add("bad"); negativeWords.add("worst"); negativeWords.add("fail");
        negativeWords.add("sad"); negativeWords.add("hate"); negativeWords.add("wrong");
    }

    public String analyzeSentiment(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "Neutral (نص فارغ)";
        }

        String cleanText = text.toLowerCase().replaceAll("[\\p{Punct}]", "");
        String[] words = cleanText.split("\\s+");

        int positiveCount = 0;
        int negativeCount = 0;

        for (String word : words) {
            if (positiveWords.contains(word)) {
                positiveCount++;
            } else if (negativeWords.contains(word)) {
                negativeCount++;
            }
        }

        if (positiveCount > negativeCount) {
            return "Positive (إيجابي)";
        } else if (negativeCount > positiveCount) {
            return "Negative (سلبي)";
        } else {
            return "Neutral (محايد)";
        }
    }
}