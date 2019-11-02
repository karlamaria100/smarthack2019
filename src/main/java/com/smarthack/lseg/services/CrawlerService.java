package com.smarthack.lseg.services;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Optional;

/**
 * Created by karla on 02/11/2019
 * File description: ...
 **/
@Service
public class CrawlerService {

    private String redit = "http://www.reddit.com/search.json";

    @Autowired
    private MongoTemplate mongoTemplate;


    public void crawlByText(String text){

        // todo make api call with the text to the link
        // parse all the results
        // get also the comments link
        // put the comments throught the azure api
        // save the results from that into the elasticsearch database


    }


    public static JSONObject makeGetRequest(String apiUrl, String bearer) throws IOException{
        HttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(apiUrl);

        if (Optional.ofNullable(bearer).isPresent())
            httpget.setHeader("Authorization", "Bearer " + bearer);

        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();

        if (response.getStatusLine().getStatusCode() == 200) {
            if (entity != null) {
                try (InputStream instream = entity.getContent()) {
                    StringWriter writer = new StringWriter();
                    IOUtils.copy(instream, writer, "UTF-8");
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
