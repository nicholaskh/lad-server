package com.lad.scrapybo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/8/26
 */
@Getter
@Setter
@Document(collection = "security")
public class SecurityBo implements Serializable {

    @Id
    private String id;

    private String title;

    private String newsType;

    private String time;

    private String text;

    private String city;

    private String sourceUrl;

    private int visitNum;

    private int shareNum;

    private int commnetNum;

    private int thumpsubNum;

    private int collectNum;

}
