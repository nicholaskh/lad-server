package com.lad.bo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * 功能描述： 咨询阅读数量表、评论、点赞、转发
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

    private int commentNum;

    private int thumpsubNum;

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

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public int getThumpsubNum() {
        return thumpsubNum;
    }

    public void setThumpsubNum(int thumpsubNum) {
        this.thumpsubNum = thumpsubNum;
    }
}
