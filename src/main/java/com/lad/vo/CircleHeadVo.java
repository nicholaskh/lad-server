package com.lad.vo;

/**
 * 功能描述：圈子头像及名称信息
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/12/20
 */
public class CircleHeadVo extends BaseVo {
    private String circleid;
    private String name;
    private String headPicture;

    public String getCircleid() {
        return circleid;
    }

    public void setCircleid(String circleid) {
        this.circleid = circleid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadPicture() {
        return headPicture;
    }

    public void setHeadPicture(String headPicture) {
        this.headPicture = headPicture;
    }
}
