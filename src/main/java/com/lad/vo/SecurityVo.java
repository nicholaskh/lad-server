package com.lad.vo;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/8/26
 */
public class SecurityVo extends BaseVo {

    private String inforid;

    private String newsType;

    private String city;

    private String title;

    private String time;

    private String text;

    private Long readNum;

    private Long thumpsubNum;

    private Long commentNum;

    private boolean selfSub;

    public String getInforid() {
        return inforid;
    }

    public void setInforid(String inforid) {
        this.inforid = inforid;
    }

    public String getNewsType() {
        return newsType;
    }

    public void setNewsType(String newsType) {
        this.newsType = newsType;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getReadNum() {
        return readNum;
    }

    public void setReadNum(Long readNum) {
        this.readNum = readNum;
    }

    public Long getThumpsubNum() {
        return thumpsubNum;
    }

    public void setThumpsubNum(Long thumpsubNum) {
        this.thumpsubNum = thumpsubNum;
    }

    public Long getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Long commentNum) {
        this.commentNum = commentNum;
    }

    public boolean isSelfSub() {
        return selfSub;
    }

    public void setSelfSub(boolean selfSub) {
        this.selfSub = selfSub;
    }
}
