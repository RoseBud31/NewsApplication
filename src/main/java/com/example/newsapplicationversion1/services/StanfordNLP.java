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
        props.setProperty("pos.model", "src/main/resources/english-left3words-distsim.tagger");
        pipeline = new StanfordCoreNLP(props);
    }

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

}
