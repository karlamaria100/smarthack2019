package com.smarthack.lseg.controllers;

import com.smarthack.lseg.services.CrawlerService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by karla on 02/11/2019
 * File description: ...
 **/
@RestController
@RequestMapping("crawler")
public class CrawlerController {

    @Autowired
    private CrawlerService crawlerService;

    @PostMapping("twitter")
    public ResponseEntity<Object> crawlTwitter(@RequestParam String name){




    }

}
