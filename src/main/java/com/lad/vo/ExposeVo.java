package com.lad.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.LinkedHashSet;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2018
 * Version: 1.0
 * Time:2018/4/22
 */
@Getter
@Setter
public class ExposeVo {

    private String exposeid;

    private String title;

    private String content;
    //消息来源
    private String source;
    //来源链接
    private String sourceUrl;
    //曝光类型
    private String exposeType;
    //上传图片或视频类型， pic/video
    private String picType;

    private LinkedHashSet<String> images;

    private String video;
    //视频缩略图
    private String videoPic;
    //阅读
    private int visitNum;
    //分享转发
    private int shareNum;
    //评论
    private int commnetNum;
    //点赞
    private int thumpsubNum;
    //收藏
    private int collectNum;

    private UserBaseVo createUserVo;

    private Date createTime;

    private Date updateTime;

    private boolean selfSup;

    private boolean isCreate;
    
}
