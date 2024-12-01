package com.example.newsapplicationversion1.services;

import com.example.newsapplicationversion1.models.Article;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.Document;

import java.util.*;
import java.util.stream.Collectors;

public class StanfordNLP {
    private static StanfordCoreNLP pipeline;
    static {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos");
        pipeline = new StanfordCoreNLP(props);
    }
    private static final Map<String, List<String>> CATEGORY_MAP = Map.of(
            "Technology", List.of("technology", "artificial intelligence", "machine learning", "neural networks", "big data", "blockchain", "cloud computing", "cybersecurity", "internet of things", "virtual reality", "augmented reality", "robotics", "automation", "software development", "hardware", "quantum computing", "data science", "devops", "saas", "mobile applications", "web development", "microservices", "edge computing", "natural language processing", "data analytics", "fintech", "e-commerce", "digital transformation", "5g technology", "computer vision", "encryption"),
            "Health", List.of("health", "healthcare", "medicine", "mental health", "fitness", "nutrition", "wellness", "diet", "exercise", "disease prevention", "clinical trials", "healthtech", "medtech", "vaccines", "public health", "chronic illness", "hospital care", "biotechnology", "pharmaceuticals", "telemedicine", "treatment", "medical devices", "health insurance", "health data", "gene therapy", "medical research", "alternative medicine", "health apps", "patient care", "health policy", "surgery"),
            "Sports", List.of("sports", "football", "basketball", "soccer", "baseball", "tennis", "hockey", "rugby", "athletics", "swimming", "gymnastics", "boxing", "mixed martial arts", "cricket", "golf", "cycling", "motorsport", "e-sports", "track and field", "sportsmanship", "team sports", "individual sports", "sports injuries", "fitness", "exercise", "sports medicine", "sports equipment", "sports nutrition", "sports psychology", "training", "competition"),
            "Entertainment", List.of("movies", "music", "television", "gaming", "streaming", "theater", "comedy", "celebrities", "pop culture", "video games", "concerts", "festivals", "podcasts", "live events", "film industry", "music industry", "show business", "broadway", "documentaries", "celebrity news", "red carpet", "award shows", "blockbusters", "indie films", "cinema", "streaming platforms", "social media influencers", "fan culture", "media", "comedy shows"),
            "Business", List.of("entrepreneurship", "startups", "business strategy", "finance", "investment", "marketing", "sales", "leadership", "management", "corporate culture", "business growth", "mergers and acquisitions", "business analytics", "product development", "customer service", "supply chain", "branding", "e-commerce", "business model", "corporate governance", "business operations", "profit margins", "business plans", "market research", "strategic planning", "negotiation", "business networking", "B2B", "B2C", "business trends"),
            "Science", List.of("physics", "chemistry", "biology", "astronomy", "genetics", "climate change", "geology", "ecology", "space exploration", "earth science", "biology research", "scientific discovery", "laboratory experiments", "nanotechnology", "microbiology", "neuroscience", "genomics", "evolution", "medical science", "pharmacology", "neuroscience", "biochemistry", "meteorology", "paleontology", "marine biology", "zoology", "scientific methods", "renewable energy", "biotechnology", "environmental science"),
            "General", List.of("news", "politics", "society", "culture", "lifestyle", "education", "travel", "food", "history", "religion", "philosophy", "current events", "community", "environment", "economy", "law", "human rights", "social issues", "language", "education systems", "literature", "art", "design", "personal development", "psychology", "family", "relationships", "technology trends", "volunteering", "sustainability", "government", "charity")
    );

    public List<String> extractKeywords(String content){
        CoreDocument doc = new CoreDocument(content);
        pipeline.annotate(doc);

        return doc.sentences().stream()
                .flatMap(sentence -> sentence.lemmas().stream())
                .filter(Objects::nonNull)
                .filter(word -> !word.isEmpty())
                .filter(this::isKeyword)
                .distinct()
                .collect(Collectors.toList());
    }
    private boolean isKeyword(String word){
        if(word==null || word.isEmpty()){
            return false;
        }
        return word.length()>3 && !isStopWord(word);
    }
    private boolean isStopWord(String word){
        List<String> stopwords = List.of("a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are", "aren't", "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by", "can't", "cannot", "could", "couldn't", "did", "didn't", "do", "does", "doesn't", "doing", "don't", "down", "during", "each", "few", "for", "from", "further", "had", "hadn't", "has", "hasn't", "have", "haven't", "having", "he", "he'd", "he'll", "he's", "her", "here", "here's", "hers", "herself", "him", "himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm", "i've", "if", "in", "into", "is", "isn't", "it", "it's", "its", "itself", "let's", "me", "more", "most", "mustn't", "my", "myself", "no", "nor", "not", "of", "off", "on", "once", "only", "or", "other", "ought", "our", "ours", "ourselves", "out", "over", "own");
        return stopwords.contains(word.toLowerCase());
    }
    public String categorizeArticle(String content){
        List<String> extractedKeywords = extractKeywords(content);
        Map<String, Integer> categoryScores = new HashMap<>();

        // Initialize scores for each category
        CATEGORY_MAP.keySet().forEach(category -> categoryScores.put(category, 0));

        // Count matching keywords for each category
        for (String keyword : extractedKeywords){
            CATEGORY_MAP.forEach((category, keywords) -> {
                if (keywords.contains(keyword.toLowerCase())){
                    categoryScores.put(category, categoryScores.get(category) + 1);
                }
            });
        }
        return categoryScores.entrySet().stream()
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse("Uncategorized");
    }
}
