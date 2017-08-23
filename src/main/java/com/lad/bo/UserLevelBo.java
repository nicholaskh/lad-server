package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 功能描述： 用户等级
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/8/23
 */
@Document(collection = "userLevel")
public class UserLevelBo extends BaseBo{

    private String userid;
    //在线时间 毫秒
    private long onlineHours;
    //发起聚会数
    private int launchPartys;
    //发帖数
    private int noteNum;
    //评论数
    private int commentNum;
    //转发数
    private int transmitNum;
    //分享数
    private int shareNum;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public long getOnlineHours() {
        return onlineHours;
    }

    public void setOnlineHours(long onlineHours) {
        this.onlineHours = onlineHours;
    }

    public void setLaunchPartys(int launchPartys) {
        this.launchPartys = launchPartys;
    }

    public int getNoteNum() {
        return noteNum;
    }

    public void setNoteNum(int noteNum) {
        this.noteNum = noteNum;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public int getTransmitNum() {
        return transmitNum;
    }

    public void setTransmitNum(int transmitNum) {
        this.transmitNum = transmitNum;
    }

    public int getShareNum() {
        return shareNum;
    }

    public void setShareNum(int shareNum) {
        this.shareNum = shareNum;
    }

    public int getLaunchPartys() {
        return launchPartys;
    }
}
