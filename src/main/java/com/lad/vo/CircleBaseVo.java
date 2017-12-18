package com.lad.vo;

/**
 * 功能描述： 圈子基本信息
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/12/18
 */
public class CircleBaseVo extends BaseVo {

    private String circleid;
    private double[] position;
    private String name;
    private String tag;
    private String sub_tag;
    private String headPicture;
    private String createuid;
    private int notesSize;
    private int usersSize;

    public String getCircleid() {
        return circleid;
    }

    public void setCircleid(String circleid) {
        this.circleid = circleid;
    }

    public double[] getPosition() {
        return position;
    }

    public void setPosition(double[] position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getSub_tag() {
        return sub_tag;
    }

    public void setSub_tag(String sub_tag) {
        this.sub_tag = sub_tag;
    }

    public String getHeadPicture() {
        return headPicture;
    }

    public void setHeadPicture(String headPicture) {
        this.headPicture = headPicture;
    }

    public String getCreateuid() {
        return createuid;
    }

    public void setCreateuid(String createuid) {
        this.createuid = createuid;
    }

    public int getNotesSize() {
        return notesSize;
    }

    public void setNotesSize(int notesSize) {
        this.notesSize = notesSize;
    }

    public int getUsersSize() {
        return usersSize;
    }

    public void setUsersSize(int usersSize) {
        this.usersSize = usersSize;
    }
}
