package com.smarthack.lseg.services;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
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

    public boolean newsExists(String id){
        QueryBuilder query = QueryBuilders.termQuery("id", id);
        try {
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.explain(true).size(1).query(query);

            SearchRequest request = new SearchRequest("reddit_news");
            request.searchType(SearchType.DFS_QUERY_THEN_FETCH);
            request.source(sourceBuilder);

            SearchResponse searchResponse = client.search(request);
            return searchResponse.getHits().totalHits > 0;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    public void insertRedditNews(String title, Long createdDate, String subreddit, Long ups,
                                 Long downs, String id, String author, String subreddit_id,
                                 String permalink, JSONArray link_flair_richtext, String url) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("title", title);
            map.put("created", createdDate);
            map.put("subreddit", subreddit);
            map.put("ups", ups);
            map.put("downs", downs);
            map.put("id", id);
            map.put("author", author);
            map.put("subreddit_id", subreddit_id);
            map.put("link_flair_richtext", link_flair_richtext.toString());
            map.put("url", url);
            map.put("permalink", permalink);
            map.put("timestamp", System.currentTimeMillis());
//            PutMappingRequest request = new PutMappingRequest("reddit_news");
//            request.source(map);
//            client.indices().putMapping(request, RequestOptions.DEFAULT);
            IndexRequest request = new IndexRequest("reddit_news", "reddit").source(map);
            IndexResponse indexResponse = client.index(request);
        } catch (ElasticsearchException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
