package com.smarthack.lseg.dtos;

import lombok.Builder;
import lombok.Data;

/**
 * Created by karla on 02/11/2019
 * File description: ...
 **/
@Data
@Builder
public class TextDetails {

    private String id;

    private String language;

    private String text;
}
