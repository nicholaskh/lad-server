package com.lad.vo;

import java.util.LinkedList;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/8/7
 */
public class InforVo extends BaseVo {

    private String inforid;

    private String module;

    private String className;

    private String title;

    private String source;

    private String sourceUrl;

    private LinkedList<String> imageUrls;

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

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public LinkedList<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(LinkedList<String> imageUrls) {
        this.imageUrls = imageUrls;
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

    public boolean isSelfSub() {
        return selfSub;
    }

    public void setSelfSub(boolean selfSub) {
        this.selfSub = selfSub;
    }

    public Long getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Long commentNum) {
        this.commentNum = commentNum;
    }
}
