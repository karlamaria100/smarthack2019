package com.smarthack.lseg.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.*;

/**
 * Created by karla on 02/11/2019
 * File description: ...
 **/
public class Helpers {

    public static JSONObject makeGetRequest(String apiUrl, String headerText, String headerValue) throws IOException {
        HttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(apiUrl);

        if (Optional.ofNullable(headerText).isPresent())
            httpget.setHeader(headerText, headerValue);

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
        } else { }
        return new JSONObject();
    }


    public static JSONObject makePOSTRequest(String apiUrl, Map<String, Object> params, String headerText, String headerValue) throws IOException {

        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(apiUrl);

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            Object object = params.get(key);
            nvps.add(new BasicNameValuePair(key, object==null?null:object.toString()));
        }
        httppost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        if (Optional.ofNullable(headerText).isPresent())
            httppost.setHeader(headerText, headerValue);

        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();

        if (response.getStatusLine().getStatusCode() == 200) {
            if (entity != null) {
                try (InputStream instream = entity.getContent()) {
                    StringWriter writer = new StringWriter();
                    org.apache.commons.io.IOUtils.copy(instream, writer, "UTF-8");
                    return new JSONObject(writer.toString());
                } catch (Exception e) {
                    return new JSONObject();
                }
            }
        } else { }

        return new JSONObject();
    }

}
