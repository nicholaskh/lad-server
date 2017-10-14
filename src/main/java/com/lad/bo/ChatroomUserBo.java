package com.lad.bo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * 功能描述：聊天室用户及昵称
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/10/11
 */
@Document(collection = "chatroomUser")
public class ChatroomUserBo implements Serializable {

    @Id
    private String id;

    private String chatroomid;

    //0  未删除 ； 1 删除
    private Integer deleted = 0;

    //用户id和昵称
    private LinkedHashMap<String, String> nicknames = new LinkedHashMap<>();

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

    public LinkedHashMap<String, String> getNicknames() {
        return nicknames;
    }

    public void setNicknames(LinkedHashMap<String, String> nicknames) {
        this.nicknames = nicknames;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}
