package com.lad.bo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedHashSet;

/**
 * 功能描述：曝光台信息
 * Version: 1.0
 * Time:2018/4/22
 */
@Setter
@Getter
@Document(collection = "expose")
public class ExposeBo extends BaseBo {

    private String title;

    private String content;
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

}
