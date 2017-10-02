package com.lad.vo;

import java.util.LinkedHashSet;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/10/2
 */
public class PartyListVo extends BaseVo {

    private String partyid;

    private String title;
    //聚会背景图片
    private String backPic;

    private LinkedHashSet<String> photos;

    private String video;
    //视频缩略图
    private String videoPic;
    //聚会时间
    private LinkedHashSet<String> startTime;
    //具体地点，线上必填
    private String addrInfo;
    //线下地标
    private String landmark;
    //聚会状态 0 已发起，1 进行中， 2 报名结束 ，3 活动结束 5 已取消
    private int status;

    private int userNum;

    public String getPartyid() {
        return partyid;
    }

    public void setPartyid(String partyid) {
        this.partyid = partyid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackPic() {
        return backPic;
    }

    public void setBackPic(String backPic) {
        this.backPic = backPic;
    }

    public LinkedHashSet<String> getPhotos() {
        return photos;
    }

    public void setPhotos(LinkedHashSet<String> photos) {
        this.photos = photos;
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

    public LinkedHashSet<String> getStartTime() {
        return startTime;
    }

    public void setStartTime(LinkedHashSet<String> startTime) {
        this.startTime = startTime;
    }

    public String getAddrInfo() {
        return addrInfo;
    }

    public void setAddrInfo(String addrInfo) {
        this.addrInfo = addrInfo;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUserNum() {
        return userNum;
    }

    public void setUserNum(int userNum) {
        this.userNum = userNum;
    }
}
