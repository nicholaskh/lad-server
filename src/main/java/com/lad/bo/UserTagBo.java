package com.lad.bo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * 功能描述：用户标签
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/28
 */
@Document(collection = "userTag")
public class UserTagBo implements Serializable {

    @Id
    private String id;

    private String userid;

    private String tagName;

    //标签分类 0 用户收藏专用标签
    private int tagType;

    // 标签被使用次数
    private long tagTimes;


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

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public int getTagType() {
        return tagType;
    }

    public void setTagType(int tagType) {
        this.tagType = tagType;
    }

    public long getTagTimes() {
        return tagTimes;
    }

    public void setTagTimes(long tagTimes) {
        this.tagTimes = tagTimes;
    }
}
