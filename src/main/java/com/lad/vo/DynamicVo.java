package com.lad.vo;

import java.util.Date;
import java.util.LinkedHashSet;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/23
 */
public class DynamicVo extends BaseVo {

    private String msgid;

    private String title;

    private LinkedHashSet<String> photos;

    private String content;
    //转发量
    private int transNum;
    //评论数量
    private int commentNum;
    //点赞数量
    private int thumpNum;

    private String userid;

    private String userPic;

    //地理位置
    private String landmark;

    private Boolean isMyThumbsup;
    //原作者
    private String owner;
    //来源类型
    private int sourceType;
    //来源id
    private String sourceid;

    private int type;

    //转发或分享时点评内容
    private String view;
    //经纬度
    private double[] postion;

    private String picType;
    //视频缩略图
    private String videoPic;

    private String video;

    private String circleid;

    private String circleName;

    private Date time;

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getMyThumbsup() {
        return isMyThumbsup;
    }

    public void setMyThumbsup(Boolean myThumbsup) {
        isMyThumbsup = myThumbsup;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserPic() {
        return userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceid() {
        return sourceid;
    }

    public void setSourceid(String sourceid) {
        this.sourceid = sourceid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public LinkedHashSet<String> getPhotos() {
        return photos;
    }

    public void setPhotos(LinkedHashSet<String> photos) {
        this.photos = photos;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public double[] getPostion() {
        return postion;
    }

    public void setPostion(double[] postion) {
        this.postion = postion;
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

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
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

    public String getCircleid() {
        return circleid;
    }

    public void setCircleid(String circleid) {
        this.circleid = circleid;
    }

    public String getCircleName() {
        return circleName;
    }

    public void setCircleName(String circleName) {
        this.circleName = circleName;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
