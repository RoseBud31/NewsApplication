package com.example.newsapplicationversion1.services;

import com.example.newsapplicationversion1.dao.*;
import com.example.newsapplicationversion1.models.Article;
import com.example.newsapplicationversion1.models.User;
import com.example.newsapplicationversion1.models.UserArticleInteraction;
import com.example.newsapplicationversion1.models.UserPreferences;
import com.example.newsapplicationversion1.session.SessionManager;

import java.util.*;
import java.util.stream.Collectors;

public class RecommendationEngine {
    private static final Map<String, List<String>> CATEGORY_MAP = Map.of(
            "Technology", List.of("technology", "artificial intelligence", "machine learning", "neural networks", "big data", "blockchain", "cloud computing", "cybersecurity", "internet of things", "virtual reality", "augmented reality", "robotics", "automation", "software development", "hardware", "quantum computing", "data science", "devops", "saas", "mobile applications", "web development", "microservices", "edge computing", "natural language processing", "data analytics", "fintech", "e-commerce", "digital transformation", "5g technology", "computer vision", "encryption"),
            "Health", List.of("health", "healthcare", "medicine", "mental health", "fitness", "nutrition", "wellness", "diet", "exercise", "disease prevention", "clinical trials", "healthtech", "medtech", "vaccines", "public health", "chronic illness", "hospital care", "biotechnology", "pharmaceuticals", "telemedicine", "treatment", "medical devices", "health insurance", "health data", "gene therapy", "medical research", "alternative medicine", "health apps", "patient care", "health policy", "surgery"),
            "Sports", List.of("sports", "football", "basketball", "soccer", "baseball", "tennis", "hockey", "rugby", "athletics", "swimming", "gymnastics", "boxing", "mixed martial arts", "cricket", "golf", "cycling", "motorsport", "e-sports", "track and field", "sportsmanship", "team sports", "individual sports", "sports injuries", "fitness", "exercise", "sports medicine", "sports equipment", "sports nutrition", "sports psychology", "training", "competition"),
            "Entertainment", List.of("movies", "music", "television", "gaming", "streaming", "theater", "comedy", "celebrities", "pop culture", "video games", "concerts", "festivals", "podcasts", "live events", "film industry", "music industry", "show business", "broadway", "documentaries", "celebrity news", "red carpet", "award shows", "blockbusters", "indie films", "cinema", "streaming platforms", "social media influencers", "fan culture", "media", "comedy shows"),
            "Business", List.of("entrepreneurship", "startups", "business strategy", "finance", "investment", "marketing", "sales", "leadership", "management", "corporate culture", "business growth", "mergers and acquisitions", "business analytics", "product development", "customer service", "supply chain", "branding", "e-commerce", "business model", "corporate governance", "business operations", "profit margins", "business plans", "market research", "strategic planning", "negotiation", "business networking", "B2B", "B2C", "business trends"),
            "Science", List.of("physics", "chemistry", "biology", "astronomy", "genetics", "climate change", "geology", "ecology", "space exploration", "earth science", "biology research", "scientific discovery", "laboratory experiments", "nanotechnology", "microbiology", "neuroscience", "genomics", "evolution", "medical science", "pharmacology", "neuroscience", "biochemistry", "meteorology", "paleontology", "marine biology", "zoology", "scientific methods", "renewable energy", "biotechnology", "environmental science"),
            "General", List.of("news", "politics", "society", "culture", "lifestyle", "education", "travel", "food", "history", "religion", "philosophy", "current events", "community", "environment", "economy", "law", "human rights", "social issues", "language", "education systems", "literature", "art", "design", "personal development", "psychology", "family", "relationships", "technology trends", "volunteering", "sustainability", "government", "charity")
    );

    private Map<String, Double> categoryScores = new HashMap<>();
    User currentUser = SessionManager.currentUser;
    UserPreferencesDAO userPreferencesDAO = new UserPreferencesDAOImpl();
    UserArticleInteractionDAO userArticleInteractionDAO = new UserArticleInteractionDAOImpl();
    ArticleDAO articleDAO = new ArticleDAOImpl();
    StanfordNLP stanfordNLP = new StanfordNLP();
    List<String> categories = userPreferencesDAO.getUserPreferences(currentUser.getUserId()).getPreferredCategories();
    List<UserArticleInteraction> readingHistory= userArticleInteractionDAO.readInteractionsForUser(currentUser.getUserId());


    public String categorizeArticle(String content){
        List<String> extractedKeywords = stanfordNLP.extractKeywords(content);
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
                .orElse("General");
    }

    public List<Article> recommendArticles(int articleCount){
        List<Article> recommendedArticles = new ArrayList<>();
        for (UserArticleInteraction interaction : readingHistory){
            int articleId = interaction.getArticleId();
            int timeSpent = interaction.getTimeSpentSeconds();
            String interactionType = interaction.getInteractionType();
            String category = articleDAO.getArticle(articleId).getCategory();
            categoryScores.put(category, calculateCategoryScore(timeSpent, interactionType, category));
        }
        // Normalize category scores and determine the number of articles per category
        double totalScore = categoryScores.values().stream().mapToDouble(Double::doubleValue).sum();
        Map<String, Integer> articlesPerCategory = new HashMap<>();

        for (Map.Entry<String, Double> entry : categoryScores.entrySet()) {
            String category = entry.getKey();
            double proportion = entry.getValue() / totalScore;
            int numArticles = (int) Math.ceil(proportion * articleCount); // Calculate proportion
            articlesPerCategory.put(category, numArticles);
        }

        // Fetch and sort articles per category
        for (Map.Entry<String, Integer> entry : articlesPerCategory.entrySet()) {
            String category = entry.getKey();
            int numArticles = entry.getValue();

            List<Article> articles = articleDAO.getArticlesByCategory(category).stream()
                    .sorted(Comparator.comparing(Article::getPublishedDate).reversed())
                    .limit(numArticles)
                    .toList();

            recommendedArticles.addAll(articles);
        }

        // Sort by freshness globally and limit to articleCount
        return recommendedArticles.stream()
                .sorted(Comparator.comparing(Article::getPublishedDate).reversed())
                .limit(articleCount)
                .collect(Collectors.toList());

    }

    private Double calculateCategoryScore(int timeSpent, String interactionType, String category) {
        double categoryScore = categoryScores.get(category);
        if (categories.contains(category)){
            categoryScore = categoryScore+3;
        }
        if (interactionType.equals("liked")){
            categoryScore = categoryScore+2;
        }
        categoryScore = categoryScore + (timeSpent/60)*0.5;
        return categoryScore;
    }


}
