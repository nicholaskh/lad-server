package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 功能描述： 用户申请加入聊天室信息
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/12/24
 */

@Document(collection = "userChatroom")
public class UserChatroomBo extends BaseBo {

    private String userid;

    private String chatroomid;
    // 0申请加入， 1 同意， -1拒绝
    private int status;

    private String reason;

    private String refuse;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getChatroomid() {
        return chatroomid;
    }

    public void setChatroomid(String chatroomid) {
        this.chatroomid = chatroomid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRefuse() {
        return refuse;
    }

    public void setRefuse(String refuse) {
        this.refuse = refuse;
    }
}
