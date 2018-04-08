package com.lad.scrapybo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2018
 * Version: 1.0
 * Time:2018/4/5
 */
@Getter
@Setter
@Document(collection = "yanglao")
public class YanglaoBo extends BaseInforBo {

    private LinkedList<String> imageUrls;

    private String time;

    private String text;

    private int num;

}
