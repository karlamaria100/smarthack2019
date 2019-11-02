package com.smarthack.lseg.models;

import lombok.Data;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by karla on 02/11/2019
 * File description: ...
 **/
@Data
@Document("ftse")
@TypeAlias("ftse")
public class Ftse {

    private String name;

    private String symbol;

}
