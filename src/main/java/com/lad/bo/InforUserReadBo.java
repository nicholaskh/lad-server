package com.lad.bo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.LinkedHashSet;

/**
 * 功能描述： 用于阅读分类信息
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/10/12
 */
@Document(collection = "inforUserRead")
public class InforUserReadBo implements Serializable {
    @Id
    private String id;

    private String userid;
    //已经阅读过的健康
    private LinkedHashSet<String> healths = new LinkedHashSet<>();
    //安全
    private LinkedHashSet<String> securitys = new LinkedHashSet<>();
    //广播
    private LinkedHashSet<String> radios = new LinkedHashSet<>();
    //视频
    private LinkedHashSet<String> videos = new LinkedHashSet<>();

    private Integer deleted = 0;

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

    public LinkedHashSet<String> getHealths() {
        return healths;
    }

    public void setHealths(LinkedHashSet<String> healths) {
        this.healths = healths;
    }

    public LinkedHashSet<String> getSecuritys() {
        return securitys;
    }

    public void setSecuritys(LinkedHashSet<String> securitys) {
        this.securitys = securitys;
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

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}
