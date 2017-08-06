package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedHashSet;

/**
 * 功能描述： 资讯订阅收藏类
 * Version: 1.0
 * Time:2017/8/1
 */
@Document(collection = "subscription")
public class InforSubscriptionBo extends BaseBo {

    private String userid;
    //咨询订阅, 订阅是小分类
    private LinkedHashSet<String> subscriptions = new LinkedHashSet<>();
    //咨询收藏，收藏是单条咨询
    private LinkedHashSet<String> collects = new LinkedHashSet<>();
    //类型
    private int type;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public LinkedHashSet<String> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(LinkedHashSet<String> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public LinkedHashSet<String> getCollects() {
        return collects;
    }

    public void setCollects(LinkedHashSet<String> collects) {
        this.collects = collects;
    }
}
