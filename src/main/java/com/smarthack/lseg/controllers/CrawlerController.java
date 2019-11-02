package com.smarthack.lseg.controllers;

import com.smarthack.lseg.services.CrawlerService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by karla on 02/11/2019
 * File description: ...
 **/
@RestController
@RequestMapping("crawler")
public class CrawlerController {

    @Autowired
    private CrawlerService crawlerService;

    @GetMapping("reddit")
    public Map<String, Object> crawlTwitter(@RequestParam String name){
        HashMap<String, Object> map = new HashMap<>();
        crawlerService.crawlByText(name);
        map.put("status", HttpStatus.ACCEPTED);
        return map;
    }

}
