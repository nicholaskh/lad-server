package com.lad.vo;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/10/9
 */
public class VideoVo extends BaseVo {

    private String inforid;

    private String sourceUrl;

    private String title;

    private String source;

    private String url;

    private String picture;

    private String module;

    private String className;

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

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
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
