package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 功能描述：二级分类资讯总体的热度，以及180天内的热度， 视频和广播
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/12/10
 */
@Document(collection = "inforGroupRecom")
public class InforGroupRecomBo extends BaseBo {

    private String module;

    private String className;

    private int type;

    private long totalNum;

    private long halfyearNum;

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
}
