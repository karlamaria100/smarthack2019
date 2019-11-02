package com.smarthack.lseg.services;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by karla on 02/11/2019
 * File description: ...
 **/
@Service
public class ElasticsearchService {

    private RestHighLevelClient client;

    private DateFormat format = new SimpleDateFormat("yy-MM-dd", Locale.ENGLISH);

    public ElasticsearchService() {
        try {
            client  = new RestHighLevelClient(
                    RestClient.builder(new HttpHost("172.16.77.8", 9200, "http")).setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
                        @Override
                        public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                            return requestConfigBuilder.setConnectTimeout(120000)
                                    .setSocketTimeout(120000);
                        }
                    }).setMaxRetryTimeoutMillis(120000));
            GetIndexRequest redditResult = new GetIndexRequest();
            redditResult.indices("reddit_results");
            GetIndexRequest twitterResult = new GetIndexRequest();
            twitterResult.indices("twitter_results");
            GetIndexRequest redditNews = new GetIndexRequest();
            redditNews.indices("reddit_news");
            GetIndexRequest twitterNews = new GetIndexRequest();
            twitterNews.indices("twitter_news");
            if(!client.indices().exists(redditResult)){
                createIndex("reddit_results");
            }
            if(!client.indices().exists(twitterResult)){
                createIndex("twitter_results");
            }
            if(!client.indices().exists(redditNews)){
                createIndex("reddit_news");
            }
            if(!client.indices().exists(twitterNews)){
                createIndex("twitter_news");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void createIndex(String name)  {
        try {
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(name);
            client.indices().create(createIndexRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void insertRedditNews(String title, Long createdDate, String subreddit, Long ups, Long downs) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("title", title);
            map.put("createdDate", createdDate);
            map.put("subreddit", subreddit);
            map.put("ups", ups);
            map.put("downs", downs);
            map.put("timestamp", System.currentTimeMillis());
            IndexRequest request = new IndexRequest("reddit_news", "reddit").source(map);
            IndexResponse indexResponse = client.index(request);
        } catch (ElasticsearchException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
