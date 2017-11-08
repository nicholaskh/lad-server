package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 功能描述： 参与聚会的人员
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/8/19
 */
@Document(collection = "partyUser")
public class PartyUserBo extends BaseBo {

    private String userid;

    private String partyid;

    private String joinPhone;

    private String joinInfo;

    private String refuseInfo;

    private double amount;

    private int userNum;

    // 0 申请， 1已加入 2 拒绝
    private int status;
    //0 未收藏，1 收藏
    private int collectParty;
    //用户是否删除记录信息  0 未删除 ， 1 已删除
    private int userDelete;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public int getCollectParty() {
        return collectParty;
    }

    public void setCollectParty(int collectParty) {
        this.collectParty = collectParty;
    }

    public int getUserDelete() {
        return userDelete;
    }

    public void setUserDelete(int userDelete) {
        this.userDelete = userDelete;
    }
}
