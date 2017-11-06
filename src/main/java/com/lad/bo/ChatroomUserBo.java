package com.lad.bo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * 功能描述：用户聊天室个性设置
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/10/11
 */
@Document(collection = "chatroomUser")
public class ChatroomUserBo implements Serializable {

    @Id
    private String id;

    private String userid;

    private String chatroomid;

    //是否开启免打扰模式 true 是； false 否
    private boolean isDisturb;

    //是否显示昵称  true 是； false 否
    private boolean isShowNick;

    //0  未删除 ； 1 删除
    private Integer deleted = 0;

    //用户在昵称
    private String nickname;
    
    //用户名称
    private String username;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChatroomid() {
        return chatroomid;
    }

    public void setChatroomid(String chatroomid) {
        this.chatroomid = chatroomid;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }


    public boolean isDisturb() {
        return isDisturb;
    }

    public void setDisturb(boolean disturb) {
        isDisturb = disturb;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isShowNick() {
        return isShowNick;
    }

    public void setShowNick(boolean showNick) {
        isShowNick = showNick;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
