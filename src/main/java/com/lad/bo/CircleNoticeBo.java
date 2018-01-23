package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedHashSet;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2018
 * Version: 1.0
 * Time:2018/1/4
 */
@Document(collection = "circleNotice")
public class CircleNoticeBo extends BaseBo {

    private String title;

    private String content;
    //圈子id
    private String circleid;

    //0 添加， 1修改，2删除
    private int type;

    //0 圈子公告， 1 群公告
    private int noticeType;
    //群聊id ，因需求变更，群聊公告和圈子公告一直，添加群聊公告类型
    private String chatroomid;


    private LinkedHashSet<String> images = new LinkedHashSet<>();

    private LinkedHashSet<String> unReadUsers;

    private LinkedHashSet<String> readUsers = new LinkedHashSet<>();


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

    public String getCircleid() {
        return circleid;
    }

    public void setCircleid(String circleid) {
        this.circleid = circleid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public LinkedHashSet<String> getUnReadUsers() {
        return unReadUsers;
    }

    public void setUnReadUsers(LinkedHashSet<String> unReadUsers) {
        this.unReadUsers = unReadUsers;
    }

    public LinkedHashSet<String> getReadUsers() {
        return readUsers;
    }

    public void setReadUsers(LinkedHashSet<String> readUsers) {
        this.readUsers = readUsers;
    }

    public LinkedHashSet<String> getImages() {
        return images;
    }

    public void setImages(LinkedHashSet<String> images) {
        this.images = images;
    }

    public int getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(int noticeType) {
        this.noticeType = noticeType;
    }

    public String getChatroomid() {
        return chatroomid;
    }

    public void setChatroomid(String chatroomid) {
        this.chatroomid = chatroomid;
    }
}
