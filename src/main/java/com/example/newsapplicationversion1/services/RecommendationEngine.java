package com.example.newsapplicationversion1.services;

import com.example.newsapplicationversion1.dao.*;
import com.example.newsapplicationversion1.models.Article;
import com.example.newsapplicationversion1.models.User;
import com.example.newsapplicationversion1.models.UserArticleInteraction;
import com.example.newsapplicationversion1.session.SessionManager;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class RecommendationEngine {
    private static final Map<String, List<String>> CATEGORY_MAP = Map.of(
            "Technology", List.of("technology","tech", "artificial intelligence", "machine learning", "neural networks", "big data", "blockchain", "cloud computing", "cybersecurity", "internet of things", "virtual reality", "augmented reality", "robotics", "automation", "software development", "hardware", "quantum computing", "data science", "devops", "saas", "mobile applications", "web development", "microservices", "edge computing", "natural language processing", "data analytics", "fintech", "e-commerce", "digital transformation", "5g technology", "computer vision", "encryption"),
            "Health", List.of("health", "healthcare", "medicine", "mental health", "fitness", "nutrition", "wellness", "diet", "exercise", "disease prevention", "clinical trials", "healthtech", "medtech", "vaccines", "public health", "chronic illness", "hospital care", "biotechnology", "pharmaceuticals", "telemedicine", "treatment", "medical devices", "health insurance", "health data", "gene therapy", "medical research", "alternative medicine", "health apps", "patient care", "health policy", "surgery", "outbreak", "pandemic", "sick", "disease"),
            "Sports", List.of("sports", "football", "basketball", "soccer", "baseball", "tennis", "hockey", "rugby", "athletics", "swimming", "gymnastics", "boxing", "mixed martial arts", "cricket", "golf", "cycling", "motorsport", "e-sports", "track and field", "sportsmanship", "team sports", "individual sports", "sports injuries", "fitness", "exercise", "sports medicine", "sports equipment", "sports nutrition", "sports psychology", "training", "competition"),
            "Entertainment", List.of("movie", "film", "forest", "storyline", "young", "families", "enchanted", "family", "journeys", "friendship","movies", "music", "television", "gaming", "streaming", "theater", "comedy", "celebrities", "pop culture", "video games", "concerts", "festivals", "podcasts", "live events", "film industry", "music industry", "show business", "broadway", "documentaries", "celebrity news", "red carpet", "award shows", "blockbusters", "indie films", "cinema", "streaming platforms", "social media influencers", "fan culture", "media", "comedy shows"),
            "Business", List.of("entrepreneurship", "startups", "business strategy", "finance", "investment", "marketing", "sales", "leadership", "management", "corporate culture", "business growth", "mergers and acquisitions", "business analytics", "product development", "customer service", "supply chain", "branding", "e-commerce", "business model", "corporate governance", "business operations", "profit margins", "business plans", "market research", "strategic planning", "negotiation", "business networking", "B2B", "B2C", "business trends"),
            "Science", List.of("physics", "chemistry", "biology", "astronomy", "genetics", "climate change", "geology", "ecology", "space exploration", "earth science", "biology research", "scientific discovery", "laboratory experiments", "nanotechnology", "microbiology", "neuroscience", "genomics", "evolution", "medical science", "pharmacology", "neuroscience", "biochemistry", "meteorology", "paleontology", "marine biology", "zoology", "scientific methods", "renewable energy", "biotechnology", "environmental science"),
            "General", List.of("news", "politics", "society", "culture", "lifestyle", "education", "travel", "food", "history", "religion", "philosophy", "current events", "community", "environment", "economy", "law", "human rights", "social issues", "language", "education systems", "literature", "art", "design", "personal development", "psychology", "family", "relationships", "technology trends", "volunteering", "sustainability", "government", "charity")
    );
    // Concurrency tools
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    public void shutdown() {
        executorService.shutdown();
    }
    private Map<String, Double> categoryScores = new HashMap<>();

    SimpleNLP simpleNLP = new SimpleNLP();
    User currentUser;

    public RecommendationEngine(){
        this.currentUser = SessionManager.currentUser;
        if (this.currentUser == null) {
            throw new IllegalStateException("No user is currently logged in. Cannot provide recommendations.");
        }
    }

    public String categorizeArticle(String content){
        List<String> extractedKeywords = simpleNLP.extractKeywords(content);
        Map<String, Integer> categoryScores = new HashMap<>();

        // Initialize scores for each category
        CATEGORY_MAP.keySet().forEach(category -> categoryScores.put(category, 0));
        System.out.println("Extracted Keywords: " + extractedKeywords);

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

    public Future<List<Article>> recommendArticles(int articleCount, User user) {
        return executorService.submit(() -> {
            try {
                if (user == null) {
                    throw new IllegalArgumentException("User must be provided for recommendations.");
                }

                UserArticleInteractionDAO userArticleInteractionDAO = new UserArticleInteractionDAOImpl();
                List<UserArticleInteraction> readingHistory = userArticleInteractionDAO.readInteractionsForUser(user.getUserId());
                List<Article> recommendedArticles = new ArrayList<>();
                ArticleDAO articleDAO = new ArticleDAOImpl();

                if (readingHistory == null || readingHistory.isEmpty()) {
                    return null;
                }

                Map<String, Double> categoryScores = new HashMap<>();

                for (UserArticleInteraction interaction : readingHistory) {
                    int articleId = interaction.getArticleId();
                    int timeSpent = interaction.getTimeSpentSeconds();
                    String interactionType = interaction.getInteractionType();

                    Article article = articleDAO.getArticle(articleId);
                    if (article == null) {
                        continue;
                    }

                    String category = article.getCategory();
                    double categoryScore = calculateCategoryScore(timeSpent, interactionType, category, user, categoryScores);
                    categoryScores.put(category, categoryScores.getOrDefault(category, 0.0) + categoryScore);
                }
                System.out.println(categoryScores);
                double totalScore = categoryScores.values().stream().mapToDouble(Double::doubleValue).sum();
                if (totalScore == 0) {
                    return null;
                }

                Map<String, Integer> articlesPerCategory = new HashMap<>();
                for (Map.Entry<String, Double> entry : categoryScores.entrySet()) {
                    String category = entry.getKey();
                    double proportion = entry.getValue() / totalScore;
                    int numArticles = (int) Math.ceil(proportion * articleCount);
                    articlesPerCategory.put(category, numArticles);
                }

                Set<Article> uniqueArticles = new HashSet<>();  // Ensures no duplicates

                for (Map.Entry<String, Integer> entry : articlesPerCategory.entrySet()) {
                    String category = entry.getKey();
                    int numArticles = entry.getValue();

                    List<Article> articles = articleDAO.getArticlesByCategory(category);
                    if (articles == null || articles.isEmpty()) {
                        continue;
                    }

                    articles.stream()
                            .sorted(Comparator.comparing(Article::getPublishedDate).reversed())
                            .limit(numArticles)
                            .forEach(uniqueArticles::add);  // Add to the Set to avoid duplicates
                }

                // Convert Set to List if needed
                recommendedArticles = new ArrayList<>(uniqueArticles);

                return recommendedArticles;
            } catch (Exception e) {
                e.printStackTrace();
                return Collections.emptyList();
            }
        });
    }

    private double calculateCategoryScore(int timeSpent, String interactionType, String category, User user, Map<String, Double> categoryScores) {
        UserPreferencesDAO userPreferencesDAO = new UserPreferencesDAOImpl();
        List<String> categories = userPreferencesDAO.getUserPreferences(user.getUserId()).getPreferredCategories();
        double categoryScore = categoryScores.getOrDefault(category, 0.0);

        if (categories != null && categories.contains(category)) {
            categoryScore += 3;
        }
        if ("liked".equals(interactionType)) {
            categoryScore += 2;
        }
        categoryScore += (timeSpent / 60) * 0.5;
        return categoryScore;
    }
}
