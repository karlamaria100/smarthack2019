package com.smarthack.lseg.services;

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

    public void crawlByText(String text){
        try {

            // todo make api call with the text to the link
            // parse all the results
            // get also the comments link
            // put the comments throught the azure api
            // save the results from that into the elasticsearch database
            JSONObject jsonObject = makeGetRequest(reddit + "?q=" + URLEncoder.encode(text,"UTF-8"), null);
            System.out.println(jsonObject);
            JSONArray results = jsonObject.getJSONObject("data").getJSONArray("children");
            for(int i = 0; i < results.length(); i++){
                insertRedditResults(results.getJSONObject(i));
            }



        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void insertRedditResults(JSONObject result){
        elasticsearchService.insertRedditNews(result.getString("title"), result.getLong("createDate"));
    }


    public static JSONObject makeGetRequest(String apiUrl, String bearer) throws IOException{
        HttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(apiUrl);

        if (Optional.ofNullable(bearer).isPresent())
            httpget.setHeader("Authorization", "Bearer " + bearer);
        httpget.setHeader("User-agent", "myuseragent_hope it work ");

        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();

        if (response.getStatusLine().getStatusCode() == 200) {
            if (entity != null) {
                try (InputStream instream = entity.getContent()) {
                    ByteArrayOutputStream writer = new ByteArrayOutputStream();
                    IOUtils.copy(instream, writer);
                    return new JSONObject(writer.toString());
                } catch (Exception e) {
                    return new JSONObject();
                }
            }
        } else {
        }
        return new JSONObject();
    }

}
