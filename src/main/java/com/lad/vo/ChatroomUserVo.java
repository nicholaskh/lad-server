package com.lad.vo;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Time:2017/10/1
 */
public class ChatroomUserVo extends BaseVo{

    private String userid;

    private String username;

    private String userPic;

    private int role;

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

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getUserPic() {
        return userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }
}
