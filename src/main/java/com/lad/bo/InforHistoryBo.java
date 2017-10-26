package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 功能描述：资讯热度历史记录
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/10/5
 */
@Document(collection = "inforHistory")
public class InforHistoryBo extends BaseBo{
    //need index
    private String inforid;
    //need index
    private long dayNum;
    //阅读时间 当天零点的时间戳  need index
    private Date readDate;
    //need index
    private int type;
    //need index
    private String module;

    public String getInforid() {
        return inforid;
    }

    public void setInforid(String inforid) {
        this.inforid = inforid;
    }

    public long getDayNum() {
        return dayNum;
    }

    public void setDayNum(long dayNum) {
        this.dayNum = dayNum;
    }

    public Date getReadDate() {
        return readDate;
    }

    public void setReadDate(Date readDate) {
        this.readDate = readDate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }
}