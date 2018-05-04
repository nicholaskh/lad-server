package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 功能描述： 申请加群聊或加圈子时的理由
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/7/5
 */

@Document(collection = "reason")
public class ReasonBo extends BaseBo {
    //申请加入的圈子
    private String circleid;
    //添加理由
    private String reason;
    //拒绝理由
    private String refues;
    //状态，0 表示申请； 1 表示通过， -1表示b拒绝
    private int status;

    private boolean isNotice;

    private String chatroomid;

    //0 圈子申请， 1 群聊申请
    private int reasonType;

    //是否是圈主或圈子管理员邀请，邀请标识
    private boolean isMasterApply;

    //圈子内未读数据，为了减少数据冗余，放到reason表中
    private int unReadNum;
    
    /**
     * 是否通过聚会页面加入圈子
     * 0 正常加入圈子，
     * 1 通过聚会页面加入圈子 ,
     * 2 通过二维码扫描进入群聊,当 reasonType为1时有效
     */
    private int addType;
    //id
    private String operUserid;

    private String partyid;

    public String getCircleid() {
        return circleid;
    }

    public void setCircleid(String circleid) {
        this.circleid = circleid;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRefues() {
        return refues;
    }

    public void setRefues(String refues) {
        this.refues = refues;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isNotice() {
        return isNotice;
    }

    public void setNotice(boolean notice) {
        isNotice = notice;
    }

    public String getChatroomid() {
        return chatroomid;
    }

    public void setChatroomid(String chatroomid) {
        this.chatroomid = chatroomid;
    }

    public boolean isMasterApply() {
        return isMasterApply;
    }

    public void setMasterApply(boolean masterApply) {
        isMasterApply = masterApply;
    }

    public int getUnReadNum() {
        return unReadNum;
    }

    public void setUnReadNum(int unReadNum) {
        this.unReadNum = unReadNum;
    }

    public int getAddType() {
        return addType;
    }

    public void setAddType(int addType) {
        this.addType = addType;
    }

    public String getPartyid() {
        return partyid;
    }

    public void setPartyid(String partyid) {
        this.partyid = partyid;
    }

    public int getReasonType() {
        return reasonType;
    }

    public void setReasonType(int reasonType) {
        this.reasonType = reasonType;
    }

    public String getOperUserid() {
        return operUserid;
    }

    public void setOperUserid(String operUserid) {
        this.operUserid = operUserid;
    }
}
