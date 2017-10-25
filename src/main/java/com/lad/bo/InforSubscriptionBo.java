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
    //安全订阅
    private LinkedHashSet<String> securitys = new LinkedHashSet<>();
    //咨询收藏，收藏是单条咨询
    private LinkedHashSet<String> collects = new LinkedHashSet<>();
    //广播订阅
    private LinkedHashSet<String> radios = new LinkedHashSet<>();
    //视频订阅
    private LinkedHashSet<String> videos = new LinkedHashSet<>();

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

    public LinkedHashSet<String> getSecuritys() {
        return securitys;
    }

    public void setSecuritys(LinkedHashSet<String> securitys) {
        this.securitys = securitys;
    }

    public LinkedHashSet<String> getCollects() {
        return collects;
    }

    public void setCollects(LinkedHashSet<String> collects) {
        this.collects = collects;
    }

    public LinkedHashSet<String> getRadios() {
        return radios;
    }

    public void setRadios(LinkedHashSet<String> radios) {
        this.radios = radios;
    }

    public LinkedHashSet<String> getVideos() {
        return videos;
    }

    public void setVideos(LinkedHashSet<String> videos) {
        this.videos = videos;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
