package com.smarthack.lseg.services;

import com.smarthack.lseg.models.Ftse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by karla on 02/11/2019
 * File description: ...
 **/
@Service
public class FTSEService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Ftse> search(String search){
        return mongoTemplate.find(Query.query(Criteria.where("name").regex(".*" + search + ".*", "i")), Ftse.class);
    }

}
