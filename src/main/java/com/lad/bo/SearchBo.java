package com.lad.bo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * 功能描述： 搜索关键词
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/4
 */
@Document(collection = "search")
public class SearchBo implements Serializable {

    @Id
    private String id;

    private String keyword;

    private long times;

    //0  未删除 ； 1 删除
    private Integer deleted = 0;

    //0 圈子， 1帖子， 2 资讯, 4 城市
    private Integer type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public long getTimes() {
        return times;
    }

    public void setTimes(long times) {
        this.times = times;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
