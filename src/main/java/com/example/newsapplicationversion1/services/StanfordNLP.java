package com.example.newsapplicationversion1.services;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.Document;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class StanfordNLP {
    public List<String> extractKeywords(String content){
        Document doc = new Document(content);
        return doc.sentences().stream()
                .flatMap(sentence -> sentence.lemmas().stream())
                .filter(word -> isKeyword(word))
                .distinct()
                .collect(Collectors.toList());
    }
    private boolean isKeyword(String word){
        return word.length()>3 && !isStopWord(word);
    }
    private boolean isStopWord(String word){
        List<String> stopwords = List.of();
        return stopwords.contains(word.toLowerCase());
    }
}
