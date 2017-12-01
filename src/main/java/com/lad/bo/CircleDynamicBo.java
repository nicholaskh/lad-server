package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 功能描述：圈子动态信息表
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/11/30
 */
@Document(collection = "circleDynamic")
public class CircleDynamicBo extends BaseBo {

    private String userid;

    private String circleid;

    //动态信息类型
    private int type;

    private String targetid;


    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
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

    public String getTargetid() {
        return targetid;
    }

    public void setTargetid(String targetid) {
        this.targetid = targetid;
    }
}
