package com.lad.vo;

import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/23
 */
public class DynamicVo extends BaseVo {

    private String msgid;

    private String title;

    private List<String> photos;

    private String content;

    private long transNum;

    private long commentNum;

    private long thumpNum;

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

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public long getTransNum() {
        return transNum;
    }

    public void setTransNum(long transNum) {
        this.transNum = transNum;
    }

    public long getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(long commentNum) {
        this.commentNum = commentNum;
    }

    public long getThumpNum() {
        return thumpNum;
    }

    public void setThumpNum(long thumpNum) {
        this.thumpNum = thumpNum;
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
}
