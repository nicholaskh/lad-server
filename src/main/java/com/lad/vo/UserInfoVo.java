package com.lad.vo;

import java.util.Date;
import java.util.LinkedHashSet;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/12/18
 */
public class UserInfoVo extends UserBaseVo {

    private LinkedHashSet<String> sports;

    private LinkedHashSet<String> musics;

    private LinkedHashSet<String> lifes;

    private LinkedHashSet<String> trips;

    private Date registTime;

    private double postion[];


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

    public Date getRegistTime() {
        return registTime;
    }

    public void setRegistTime(Date registTime) {
        this.registTime = registTime;
    }

    public double[] getPostion() {
        return postion;
    }

    public void setPostion(double[] postion) {
        this.postion = postion;
    }
}
