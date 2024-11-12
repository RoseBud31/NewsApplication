package com.example.newsapplicationversion1.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;

public class ArticleFetcher {
    public static void main (String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Articles articles = mapper.readValue("src/main/resources/com/example/newsapplicationversion1/articles.json", Articles.class);
        System.out.println(articles);
    }
}
