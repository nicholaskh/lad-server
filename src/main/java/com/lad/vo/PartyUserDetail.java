package com.lad.vo;

import java.util.LinkedHashSet;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/11/18
 */
public class PartyUserDetail extends BaseVo {

    //聚会时间
    private LinkedHashSet<String> startTime;

    private String userid;

    private String username;

    private String userPic;

    private String partyid;

    private String joinPhone;

    private String joinInfo;

    private String refuseInfo;

    private double amount;

    private int userNum;

    //地点类型 0 线上； 1线下
    private int addrType;
    //具体地点，线上必填
    private String addrInfo;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserPic() {
        return userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }

    public String getPartyid() {
        return partyid;
    }

    public void setPartyid(String partyid) {
        this.partyid = partyid;
    }

    public String getJoinPhone() {
        return joinPhone;
    }

    public void setJoinPhone(String joinPhone) {
        this.joinPhone = joinPhone;
    }

    public String getJoinInfo() {
        return joinInfo;
    }

    public void setJoinInfo(String joinInfo) {
        this.joinInfo = joinInfo;
    }

    public String getRefuseInfo() {
        return refuseInfo;
    }

    public void setRefuseInfo(String refuseInfo) {
        this.refuseInfo = refuseInfo;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getUserNum() {
        return userNum;
    }

    public void setUserNum(int userNum) {
        this.userNum = userNum;
    }

    public int getAddrType() {
        return addrType;
    }

    public void setAddrType(int addrType) {
        this.addrType = addrType;
    }

    public String getAddrInfo() {
        return addrInfo;
    }

    public void setAddrInfo(String addrInfo) {
        this.addrInfo = addrInfo;
    }

    public LinkedHashSet<String> getStartTime() {
        return startTime;
    }

    public void setStartTime(LinkedHashSet<String> startTime) {
        this.startTime = startTime;
    }
}
