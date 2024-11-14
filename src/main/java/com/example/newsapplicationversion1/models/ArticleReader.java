package com.example.newsapplicationversion1.models;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.JsonParserDelegate;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.List;

public class ArticleReader {
    public static List<Article> retrieveArticles() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Read the JSON file and map it to a List of Article objects
            return mapper.readValue(new File("src/main/java/com/example/newsapplicationversion1/models/articles.json"), new TypeReference<List<Article>>() {});
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
