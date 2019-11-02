package com.smarthack.lseg.controllers;

import com.smarthack.lseg.services.FTSEService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by karla on 02/11/2019
 * File description: ...
 **/
@RestController
@RequestMapping("/main")
public class MainController {

    private FTSEService service;

    @RequestMapping("/search")
    public Map<String, Object> searchFTPS(@RequestParam String search){
        Map<String, Object> map = new HashMap<>();
        map.put("ftse",service.search(search));
        map.put("status", HttpStatus.ACCEPTED);
        return map;
    }

}
