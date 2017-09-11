package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedHashSet;

/**
 * 功能描述： 聚会管理
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/8/27
 */
@Document(collection = "partyManage")
public class PartyManageBo extends BaseBo {

    private String partyid;

    private LinkedHashSet<String> applyUsers;

    private LinkedHashSet<String> users;

    private LinkedHashSet<String> refuseUsers;
    //访问数量
    private int visitNum;
    //分享数量
    private int shareNum;
    //收藏数量
    private int keepNum;
    //举报数量
    private int reportNum;

    public String getPartyid() {
        return partyid;
    }

    public void setPartyid(String partyid) {
        this.partyid = partyid;
    }

    public LinkedHashSet<String> getApplyUsers() {
        return applyUsers;
    }

    public void setApplyUsers(LinkedHashSet<String> applyUsers) {
        this.applyUsers = applyUsers;
    }

    public LinkedHashSet<String> getUsers() {
        return users;
    }

    public void setUsers(LinkedHashSet<String> users) {
        this.users = users;
    }

    public LinkedHashSet<String> getRefuseUsers() {
        return refuseUsers;
    }

    public void setRefuseUsers(LinkedHashSet<String> refuseUsers) {
        this.refuseUsers = refuseUsers;
    }

    public int getVisitNum() {
        return visitNum;
    }

    public void setVisitNum(int visitNum) {
        this.visitNum = visitNum;
    }

    public int getShareNum() {
        return shareNum;
    }

    public void setShareNum(int shareNum) {
        this.shareNum = shareNum;
    }

    public int getKeepNum() {
        return keepNum;
    }

    public void setKeepNum(int keepNum) {
        this.keepNum = keepNum;
    }

    public int getReportNum() {
        return reportNum;
    }

    public void setReportNum(int reportNum) {
        this.reportNum = reportNum;
    }
}
