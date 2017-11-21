package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedHashSet;

/**
 * 功能描述：个人动态
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/19
 */
@Document(collection = "dynamic")
public class DynamicBo extends BaseBo {

    //标题
    private String title;
    //内容
    private String content;
    //图片
    private LinkedHashSet<String> photos = new LinkedHashSet<>();
    //经纬度
    private double[] postion;
    //转发量
    private int transNum;
    //评论数量
    private int commentNum;
    //点赞数量
    private int thumpNum;
    //地理位置
    private String landmark;

    private String picType;
    //视频缩略图
    private String videoPic;

    //原作者
    private String owner;
    //来源类型
    private int sourceType;
    //来源id
    private String sourceid;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LinkedHashSet<String> getPhotos() {
        return photos;
    }

    public void setPhotos(LinkedHashSet<String> photos) {
        this.photos = photos;
    }

    public double[] getPostion() {
        return postion;
    }

    public void setPostion(double[] postion) {
        this.postion = postion;
    }

    public int getTransNum() {
        return transNum;
    }

    public void setTransNum(int transNum) {
        this.transNum = transNum;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public int getThumpNum() {
        return thumpNum;
    }

    public void setThumpNum(int thumpNum) {
        this.thumpNum = thumpNum;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getPicType() {
        return picType;
    }

    public void setPicType(String picType) {
        this.picType = picType;
    }

    public String getVideoPic() {
        return videoPic;
    }

    public void setVideoPic(String videoPic) {
        this.videoPic = videoPic;
    }

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getSourceid() {
        return sourceid;
    }

    public void setSourceid(String sourceid) {
        this.sourceid = sourceid;
    }
}
