package com.smarthack.lseg.services;

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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by karla on 02/11/2019
 * File description: ...
 **/
@Service
public class CrawlerService {

    private String reddit = "http://www.reddit.com/search.json";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private CognitiveAzureService cognitiveAzureService;

    public void crawlByText(String text){
        try {

            // todo make api call with the text to the link
            // parse all the results
            // get also the comments link
            // put the comments throught the azure api
            // save the results from that into the elasticsearch database
            JSONObject jsonObject = Helpers.makeGetRequest(reddit + "?q=" + URLEncoder.encode(text,"UTF-8"), null, null);
            System.out.println(jsonObject);
            JSONArray results = jsonObject.getJSONObject("data").getJSONArray("children");
            for(int i = 0; i < results.length(); i++){
                insertRedditResults(results.getJSONObject(i));
                getCognitiveResult();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getCognitiveResult() {
        cognitiveAzureService.getTextSentiment();
    }

    public void insertRedditResults(JSONObject result){
        JSONObject data = result.getJSONObject("data");
        if(!elasticsearchService.newsExists(data.getString("id")))
            elasticsearchService.insertRedditNews(data.getString("title"),
                    data.getLong("created"),
                    data.getString("subreddit"),
                    data.getLong("downs"),
                    data.getLong("ups"),
                    data.getString("id"),
                    data.getString("author"),
                    data.getString("subreddit_id"),
                    data.getString("permalink"),
                    data.getJSONArray("link_flair_richtext"),
                    data.getString("url"));
    }
}
