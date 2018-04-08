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
 * Time:2017/9/27
 */
@Getter
@Setter
@Document(collection = "video")
public class VideoBo extends BaseInforBo {

    private String url;

    private String poster;

    private int num;

    //合集第一条信息展示
    private String firstUrl;

    private String firstId;

    private int firstShare;

    private int firstComment;

    private int firstThump;

}
