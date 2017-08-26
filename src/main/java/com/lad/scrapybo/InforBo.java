package com.lad.scrapybo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * 功能描述： 咨询
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/7/31
 */
@Document(collection = "health")
public class InforBo implements Serializable {

    @Id
    private String id;

    //模块名字
    private String module;

    private String className;

    private int classNum;

    private String title;

    private String source;

    private String sourceUrl;

    private LinkedList<String> imageUrls;

    private String time;

    private String text;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getClassNum() {
        return classNum;
    }

    public void setClassNum(int classNum) {
        this.classNum = classNum;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public LinkedList<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(LinkedList<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
