package com.lad.vo;

import java.util.Date;
import java.util.LinkedHashSet;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/12/14
 */
public class CollectVo extends BaseVo {

    private String collectid;

    private String content;

    private String userid;

    private String title;

    private String path;

    private int type;
    //子分类，在url中区分文章、帖子、聚会、圈子
    private int sub_type;

    private String targetid;
    //来源
    private String source;
    //收藏来源类型，资讯类型来源分类，1 健康， 2安防， 3 广播， 4 视频， 5 圈子
    private int sourceType;

    private String collectUserid;

    private String collectUserName;

    private String collectUserPic;
    //来源的原始图片，如圈子头像，资讯、帖子第一张图片
    private String collectPic;

    private String video;

    private String videoPic;

    //用户自定义分类
    private LinkedHashSet<String> userTags = new LinkedHashSet<>();

    private Date collectTime;

    public String getCollectid() {
        return collectid;
    }

    public void setCollectid(String collectid) {
        this.collectid = collectid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSub_type() {
        return sub_type;
    }

    public void setSub_type(int sub_type) {
        this.sub_type = sub_type;
    }

    public String getTargetid() {
        return targetid;
    }

    public void setTargetid(String targetid) {
        this.targetid = targetid;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    public LinkedHashSet<String> getUserTags() {
        return userTags;
    }

    public void setUserTags(LinkedHashSet<String> userTags) {
        this.userTags = userTags;
    }

    public Date getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(Date collectTime) {
        this.collectTime = collectTime;
    }

    public String getCollectUserid() {
        return collectUserid;
    }

    public void setCollectUserid(String collectUserid) {
        this.collectUserid = collectUserid;
    }

    public String getCollectUserName() {
        return collectUserName;
    }

    public void setCollectUserName(String collectUserName) {
        this.collectUserName = collectUserName;
    }

    public String getCollectUserPic() {
        return collectUserPic;
    }

    public void setCollectUserPic(String collectUserPic) {
        this.collectUserPic = collectUserPic;
    }

    public String getCollectPic() {
        return collectPic;
    }

    public void setCollectPic(String collectPic) {
        this.collectPic = collectPic;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getVideoPic() {
        return videoPic;
    }

    public void setVideoPic(String videoPic) {
        this.videoPic = videoPic;
    }
}
