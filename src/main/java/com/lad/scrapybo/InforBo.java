package com.lad.scrapybo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * 功能描述： 咨询
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/7/31
 */
@Setter
@Getter
@Document(collection = "health")
public class InforBo extends BaseInforBo {

    private int classNum;

    private LinkedList<String> imageUrls;

    private String time;

    private String text;

    private int num;
}
