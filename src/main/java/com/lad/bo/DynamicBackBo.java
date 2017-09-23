package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;

/**
 * 功能描述：个人动态 黑名单设置
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/21
 */
@Document(collection = "dynamicBack")
public class DynamicBackBo extends BaseBo {

    private String userid;

    //我不看谁   黑名单
    private HashSet<String> notSeeBacks = new HashSet<>();

    //不让谁看我 黑名单
    private HashSet<String> notAllowBacks = new HashSet<>();


    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public HashSet<String> getNotSeeBacks() {
        return notSeeBacks;
    }

    public void setNotSeeBacks(HashSet<String> notSeeBacks) {
        this.notSeeBacks = notSeeBacks;
    }

    public HashSet<String> getNotAllowBacks() {
        return notAllowBacks;
    }

    public void setNotAllowBacks(HashSet<String> notAllowBacks) {
        this.notAllowBacks = notAllowBacks;
    }
}
