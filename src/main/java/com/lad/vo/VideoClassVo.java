package com.lad.vo;

import com.lad.bo.ThumbsupBo;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2018
 * Version: 1.0
 * Time:2018/2/1
 */
public class VideoClassVo extends BaseVo {

    private String module;

    private String title;

    private String source;

    private int totalVisit;

    private String inforid;

    private String url;

    private int shareNum;

    private int thumpsubNum;

    private int commentNum;

    private boolean selfSub;

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
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

    public int getTotalVisit() {
        return totalVisit;
    }

    public void setTotalVisit(int totalVisit) {
        this.totalVisit = totalVisit;
    }

    public String getInforid() {
        return inforid;
    }

    public void setInforid(String inforid) {
        this.inforid = inforid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getShareNum() {
        return shareNum;
    }

    public void setShareNum(int shareNum) {
        this.shareNum = shareNum;
    }

    public int getThumpsubNum() {
        return thumpsubNum;
    }

    public void setThumpsubNum(int thumpsubNum) {
        this.thumpsubNum = thumpsubNum;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public boolean isSelfSub() {
        return selfSub;
    }

    public void setSelfSub(boolean selfSub) {
        this.selfSub = selfSub;
    }
}
