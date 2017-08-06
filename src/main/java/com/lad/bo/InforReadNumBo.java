package com.lad.bo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * 功能描述： 咨询阅读数量表，由于之前咨询的是在另外一张表
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/8/5
 */
@Document(collection = "inforReadNum")
public class InforReadNumBo implements Serializable{

    @Id
    private String id;

    private String inforid;

    private String className;

    private long visitNum;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInforid() {
        return inforid;
    }

    public void setInforid(String inforid) {
        this.inforid = inforid;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public long getVisitNum() {
        return visitNum;
    }

    public void setVisitNum(long visitNum) {
        this.visitNum = visitNum;
    }
}
