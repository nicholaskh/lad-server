package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedList;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/11/24
 */
@Document(collection = "partyNotice")
public class PartyNoticeBo extends BaseBo {

    private String title;

    private String content;

    private String partyid;
    //用户
    private LinkedList<String> users = new LinkedList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPartyid() {
        return partyid;
    }

    public void setPartyid(String partyid) {
        this.partyid = partyid;
    }

    public LinkedList<String> getUsers() {
        return users;
    }

    public void setUsers(LinkedList<String> users) {
        this.users = users;
    }
}
