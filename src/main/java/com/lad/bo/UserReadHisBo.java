package com.lad.bo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * 功能描述：用户阅读资讯最后一条历史表
 * Copyright: Copyright (c) 2018
 * Version: 1.0
 * Time:2018/2/7
 */
@Document(collection = "userReadHis")
public class UserReadHisBo implements Serializable {

    @Id
    private String id;

    private String userid;
    //资讯类型
    private int inforType;

    private String module;

    private String className;
    //资讯id
    private String inforid;
    //最后一次阅读时间
    private Date lastTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getInforType() {
        return inforType;
    }

    public void setInforType(int inforType) {
        this.inforType = inforType;
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

    public String getInforid() {
        return inforid;
    }

    public void setInforid(String inforid) {
        this.inforid = inforid;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }
}
