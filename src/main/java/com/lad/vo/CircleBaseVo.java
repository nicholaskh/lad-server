package com.lad.vo;

/**
 * 功能描述： 圈子基本信息
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/12/18
 */
public class CircleBaseVo extends CircleHeadVo {

    private double[] position;
    private String tag;
    private String sub_tag;
    private String createuid;
    private int notesSize;
    private int usersSize;


    public double[] getPosition() {
        return position;
    }

    public void setPosition(double[] position) {
        this.position = position;
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
