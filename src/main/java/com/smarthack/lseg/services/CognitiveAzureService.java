package com.smarthack.lseg.services;

import com.smarthack.lseg.utils.Helpers;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.print.attribute.HashAttributeSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by karla on 02/11/2019
 * File description: ...
 **/
@Service
public class CognitiveAzureService {

    private String azureCognitive = "https://smarthacktest.cognitiveservices.azure.com";

    private String azureCognitiveKey = "02360bdc7e92455b918d21f6605287ef";

    public JSONObject getTextSentiment(Map<String, Object> map){
        try {
            return Helpers.makePOSTRequest(azureCognitive + "/text/analytics/v2.1/sentiment?showStats=true", map,"Ocp-Apim-Subscription-Key", azureCognitiveKey);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
