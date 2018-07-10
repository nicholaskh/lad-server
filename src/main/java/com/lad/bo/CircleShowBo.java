package com.lad.bo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * 功能描述：圈子最新内容展示内容
 * Copyright: Copyright (c) 2018
 * Version: 1.0
 * Time:2018/1/9
 */
@Document(collection = "circleShow")
public class CircleShowBo implements Serializable {

    @Id
    private String id;
    //0 帖子， 1聚会 ，2 资讯
    private int type;
    //资讯类型
    private int inforType;

    private String targetid;
    //圈子id
    private String circleid;
    //创建时间
    private Date createTime;
    //创建者id
    private String createuid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTargetid() {
        return targetid;
    }

    public void setTargetid(String targetid) {
        this.targetid = targetid;
    }

    public String getCircleid() {
        return circleid;
    }

    public void setCircleid(String circleid) {
        this.circleid = circleid;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getInforType() {
        return inforType;
    }

    public void setInforType(int inforType) {
        this.inforType = inforType;
    }

    public String getCreateuid() {
        return createuid;
    }

    public void setCreateuid(String createuid) {
        this.createuid = createuid;
    }
}
