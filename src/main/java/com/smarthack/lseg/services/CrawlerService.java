package com.smarthack.lseg.services;

import com.smarthack.lseg.dtos.TextDetails;
import com.smarthack.lseg.utils.Helpers;
import lombok.experimental.Helper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by karla on 02/11/2019
 * File description: ...
 **/
@Service
public class CrawlerService {

    private String reddit = "http://www.reddit.com/search.json";
    private String apiReddit = "https://api.pushshift.io/reddit/search?link_id=";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private CognitiveAzureService cognitiveAzureService;

    public void crawlByText(String text) {
        try {

            JSONObject jsonObject = Helpers.makeGetRequest(reddit + "?q=" + URLEncoder.encode(text, "UTF-8"), null, null);
            System.out.println(jsonObject);
            JSONArray results = jsonObject.getJSONObject("data").getJSONArray("children");
            for (int i = 0; i < results.length(); i++) {
                insertRedditResults(results.getJSONObject(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getCognitiveResult(TextDetails textDetails) {
        try {
            Map<String, Object> map = new HashMap<>();
            List<Map> document = new ArrayList<>();

            JSONObject data = Helpers.makeGetRequest(apiReddit + textDetails.getId(), null, null);
            JSONArray comments = data.getJSONArray("data");

            for (int i = 0; i < comments.length(); i++) {
                HashMap<String, Object> commentDetails = new HashMap<>();
                JSONObject comment = comments.getJSONObject(i);
                String kind = textDetails.getId().split("_")[0];
                if (comment.has("parent_id") && comment.get("parent_id").equals(textDetails.getId()))
                    elasticsearchService.insertRedditNews(comment.getString("body"),
                            comment.getLong("created_utc"),
                            comment.getString("subreddit"),
                            comment.getString("parent_id"),
                            comment.getLong("score"),
                            kind + "_" + comment.getString("id"),
                            comment.getString("author"),
                            comment.getString("subreddit_id"),
                            comment.has("selftext") ? comment.getString("selftext") : null);
                commentDetails.put("id", kind + "_" + comment.getString("id"));
                commentDetails.put("language", textDetails.getLanguage());
                commentDetails.put("text", comment.getString("body"));
                document.add(commentDetails);
            }
            HashMap<String, Object> commentDetails = new HashMap<>();

            commentDetails.put("id", textDetails.getId());
            commentDetails.put("language", textDetails.getLanguage());
            commentDetails.put("text", textDetails.getText());

            document.add(commentDetails);

            map.put("documents", document);
            JSONArray sentiments = cognitiveAzureService.getTextSentiment(map).getJSONArray("documents");

            for(int i = 0; i < sentiments.length(); i++){
                //save the sentiments to the db for comments
                //calculate the way we decided the influence
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertRedditResults(JSONObject result) {
        JSONObject data = result.getJSONObject("data");
        if (!elasticsearchService.newsExists(data.getString("id")) && !data.getString("selftext").equals("")) {
            elasticsearchService.insertRedditNews(data.getString("title"),
                    data.getLong("created_utc"),
                    data.getString("subreddit"),
                    null,
                    data.getLong("score"),
                    result.get("kind") + "_" + data.getString("id"),
                    data.getString("author"),
                    data.getString("subreddit_id"),
                    data.getString("url"));
            TextDetails textDetails = TextDetails.builder().text(data.getString("selftext")).id(result.get("kind") + "_" + data.getString("id")).language("en").build();
            getCognitiveResult(textDetails);
        }
    }
}
