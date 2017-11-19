package com.lad.vo;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/11/18
 */
public class PartyUserDetail extends BaseVo {

    private String userid;

    private String username;

    private String userPic;

    private String partyid;

    private String joinPhone;

    private String joinInfo;

    private String refuseInfo;

    private double amount;

    private int userNum;

    private String partyAddr;

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

    public String getPartyAddr() {
        return partyAddr;
    }

    public void setPartyAddr(String partyAddr) {
        this.partyAddr = partyAddr;
    }
}
