package com.lad.vo;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2018
 * Version: 1.0
 * Time:2018/2/1
 */
public class RadioClassVo extends BaseVo {


    private String module;

    private String title;

    private String source;

    private int totalVisit;

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getTotalVisit() {
        return totalVisit;
    }

    public void setTotalVisit(int totalVisit) {
        this.totalVisit = totalVisit;
    }
}
