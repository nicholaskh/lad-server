package com.lad.vo;

import java.util.LinkedList;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2018
 * Version: 1.0
 * Time:2018/2/4
 */
public class InforBaseVo extends BaseVo {

    private String inforid;

    private int inforType;

    private String title;

    private String module;

    private String className;
    //图片pic, 视频video
    private String type;

    private String videoPic;

    private String videoUrl;

    private LinkedList<String> imageUrls;

    private int readNum;

    private int thumpsubNum;

    private int commentNum;

    private int shareNum;

    private boolean selfSub;

    private String fromUserid;

    private String fromUserName;

    private String fromUserPic;

    private String fromUserSign;

    private String fromUserSex;

    private String fromUserBirth;

    private int fromUserLevel;

    public String getInforid() {
        return inforid;
    }

    public void setInforid(String inforid) {
        this.inforid = inforid;
    }

    public int getInforType() {
        return inforType;
    }

    public void setInforType(int inforType) {
        this.inforType = inforType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVideoPic() {
        return videoPic;
    }

    public void setVideoPic(String videoPic) {
        this.videoPic = videoPic;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public LinkedList<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(LinkedList<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public int getReadNum() {
        return readNum;
    }

    public void setReadNum(int readNum) {
        this.readNum = readNum;
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

    public int getShareNum() {
        return shareNum;
    }

    public void setShareNum(int shareNum) {
        this.shareNum = shareNum;
    }

    public boolean isSelfSub() {
        return selfSub;
    }

    public void setSelfSub(boolean selfSub) {
        this.selfSub = selfSub;
    }

    public String getFromUserid() {
        return fromUserid;
    }

    public void setFromUserid(String fromUserid) {
        this.fromUserid = fromUserid;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getFromUserPic() {
        return fromUserPic;
    }

    public void setFromUserPic(String fromUserPic) {
        this.fromUserPic = fromUserPic;
    }

    public String getFromUserSign() {
        return fromUserSign;
    }

    public void setFromUserSign(String fromUserSign) {
        this.fromUserSign = fromUserSign;
    }

    public String getFromUserSex() {
        return fromUserSex;
    }

    public void setFromUserSex(String fromUserSex) {
        this.fromUserSex = fromUserSex;
    }

    public int getFromUserLevel() {
        return fromUserLevel;
    }

    public void setFromUserLevel(int fromUserLevel) {
        this.fromUserLevel = fromUserLevel;
    }

    public String getFromUserBirth() {
        return fromUserBirth;
    }

    public void setFromUserBirth(String fromUserBirth) {
        this.fromUserBirth = fromUserBirth;
    }
}
