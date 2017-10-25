package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 功能描述：资讯总体的热度，以及180天内的热度
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/10/9
 */
@Document(collection = "inforRecom")
public class InforRecomBo extends BaseBo {

    private String inforid;
    //资讯类型
    private int type;

    private long totalNum;

    private long halfyearNum;

    private String module;


    public String getInforid() {
        return inforid;
    }

    public void setInforid(String inforid) {
        this.inforid = inforid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(long totalNum) {
        this.totalNum = totalNum;
    }

    public long getHalfyearNum() {
        return halfyearNum;
    }

    public void setHalfyearNum(long halfyearNum) {
        this.halfyearNum = halfyearNum;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }
}
