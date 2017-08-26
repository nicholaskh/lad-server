package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedHashSet;
import java.util.LinkedList;

/**
 * 功能描述： 资讯订阅收藏类
 * Version: 1.0
 * Time:2017/8/1
 */
@Document(collection = "subscription")
public class InforSubscriptionBo extends BaseBo {

    private String userid;
    //咨询订阅, 订阅是小分类
    private LinkedList<String> subscriptions = new LinkedList<>();
    //安全订阅
    private LinkedList<String> securitys = new LinkedList<>();
    //咨询收藏，收藏是单条咨询
    private LinkedHashSet<String> collects = new LinkedHashSet<>();

    private int type;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public LinkedList<String> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(LinkedList<String> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public LinkedHashSet<String> getCollects() {
        return collects;
    }

    public void setCollects(LinkedHashSet<String> collects) {
        this.collects = collects;
    }

    public LinkedList<String> getSecuritys() {
        return securitys;
    }

    public void setSecuritys(LinkedList<String> securitys) {
        this.securitys = securitys;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
