package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.ToString;

import java.util.LinkedHashSet;

/**
 * 功能描述：用户兴趣
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/13
 */
@Document(collection = "userTaste")
@ToString
public class UserTasteBo extends BaseBo {

    private String userid;

    private LinkedHashSet<String> sports = new LinkedHashSet<>();

    private LinkedHashSet<String> musics = new LinkedHashSet<>();

    private LinkedHashSet<String> lifes = new LinkedHashSet<>();

    private LinkedHashSet<String> trips = new LinkedHashSet<>();

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public LinkedHashSet<String> getSports() {
        return sports;
    }

    public void setSports(LinkedHashSet<String> sports) {
        this.sports = sports;
    }

    public LinkedHashSet<String> getMusics() {
        return musics;
    }

    public void setMusics(LinkedHashSet<String> musics) {
        this.musics = musics;
    }

    public LinkedHashSet<String> getLifes() {
        return lifes;
    }

    public void setLifes(LinkedHashSet<String> lifes) {
        this.lifes = lifes;
    }

    public LinkedHashSet<String> getTrips() {
        return trips;
    }

    public void setTrips(LinkedHashSet<String> trips) {
        this.trips = trips;
    }
}
