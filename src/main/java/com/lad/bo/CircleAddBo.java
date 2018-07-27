package com.lad.bo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * 功能描述： 圈子加入历史，是否加入过圈子
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/17
 */
@Document(collection = "circleAdd")
public class CircleAddBo implements Serializable {

    @Id
    private String id;

    private String userid;

    private String circleid;
    // 0.拒绝;1.同意
    private int status;

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

    public String getCircleid() {
        return circleid;
    }

    public void setCircleid(String circleid) {
        this.circleid = circleid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
