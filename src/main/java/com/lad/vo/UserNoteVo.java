package com.lad.vo;

/**
 * 功能描述：帖子中提到用户对象信息
 * Copyright: Copyright (c) 2018
 * Version: 1.0
 * Time:2018/1/24
 */
public class UserNoteVo extends BaseVo {

    private String backName;

    private String userName;

    private String sex;

    private String userid;

    public String getBackName() {
        return backName;
    }

    public void setBackName(String backName) {
        this.backName = backName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
