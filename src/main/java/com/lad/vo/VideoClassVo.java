package com.lad.vo;


import lombok.Getter;
import lombok.Setter;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2018
 * Version: 1.0
 * Time:2018/2/1
 */
@Getter
@Setter
public class VideoClassVo extends BaseVo {

    private String module;

    private String title;

    private String source;

    private int totalVisit;

    private String inforid;

    private String picture;

    private String url;

    private int shareNum;

    private int thumpsubNum;

    private int commentNum;

    private boolean selfSub;

    private int totalNum;

}
