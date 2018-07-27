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
	//标题
    private String title;
    //内容
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
    //状态， 后续使用到审核需要
    private int status;
}
