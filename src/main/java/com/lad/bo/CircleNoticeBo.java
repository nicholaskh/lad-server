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

    private String circleid;

    //0 添加， 1修改，2删除
    private int type;


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
}
