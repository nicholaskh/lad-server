package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/23
 */
@Document(collection = "dynamicNum")
public class DynamicNumBo extends BaseBo {

    private String userid;

    //个人动态数量 帖子数+聚会数+评论+点赞+转发
    private long number;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }
}
