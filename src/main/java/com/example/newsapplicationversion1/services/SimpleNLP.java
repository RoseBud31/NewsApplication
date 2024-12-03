package com.example.newsapplicationversion1.services;

import java.util.*;
import java.util.stream.Collectors;

public class SimpleNLP {

    public List<String> extractKeywords(String content) {
        String[] words = content.toLowerCase().split("\\W+");
        Map<String, Integer> frequencyMap = new HashMap<>();

        for (String word : words) {
            if (isKeyword(word)) {
                frequencyMap.put(word, frequencyMap.getOrDefault(word, 0) + 1);
            }
        }

        // Sort by frequency and get top keywords
        return frequencyMap.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .map(Map.Entry::getKey)
                .limit(10)
                .collect(Collectors.toList());
    }

    private boolean isKeyword(String word) {
        return word != null && word.length() > 3 && !isStopWord(word);
    }

    private boolean isStopWord(String word) {
        Set<String> stopwords = Set.of("will", "however", "the", "a", "about", "above", "after", "again", "against",
                "all", "am", "an", "and", "any", "are", "as", "at", "be", "because", "been", "before", "being",
                "below", "between", "both", "but", "by", "can", "cannot", "could", "did", "do", "does", "doing",
                "down", "during", "each", "few", "for", "from", "further", "had", "has", "have", "having", "he",
                "her", "here", "hers", "him", "himself", "his", "how", "i", "if", "in", "into", "is", "it", "its",
                "itself", "let", "me", "more", "most", "my", "myself", "no", "nor", "not", "of", "off", "on", "once",
                "only", "or", "other", "our", "ours", "ourselves", "out", "over", "own", "same", "she", "should", "so",
                "some", "such", "than", "that", "their", "them", "then", "there", "these", "they", "this", "those",
                "through", "to", "under", "until", "up", "very", "was", "we", "were", "what", "when", "where", "which",
                "while", "who", "whom", "why", "with", "would", "you", "your", "yours", "yourself", "yourselves");
        return stopwords.contains(word);
    }

}
