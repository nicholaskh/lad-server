package com.lad.vo;

import java.util.Date;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/8/5
 */
public class CommentVo extends BaseVo {

    private String commentId;

    private String content;

    private String parentid;
    //回复的用户名称
    private String parentUserName;

    private String parentUserid;

    private String userName;

    private String userid;

    private Date createTime;

    private long thumpsubCount;

    private Boolean isMyThumbsup;

    private String userHeadPic;

    private String userSex;

    private String userBirth;

    private int userLevel;

    public long getThumpsubCount() {
        return thumpsubCount;
    }

    public void setThumpsubCount(long thumpsubCount) {
        this.thumpsubCount = thumpsubCount;
    }

    public Boolean getMyThumbsup() {
        return isMyThumbsup;
    }

    public void setMyThumbsup(Boolean myThumbsup) {
        isMyThumbsup = myThumbsup;
    }

    public String getParentUserid() {
        return parentUserid;
    }

    public void setParentUserid(String parentUserid) {
        this.parentUserid = parentUserid;
    }

    public String getParentUserName() {
        return parentUserName;
    }

    public void setParentUserName(String parentUserName) {
        this.parentUserName = parentUserName;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUserHeadPic() {
        return userHeadPic;
    }

    public void setUserHeadPic(String userHeadPic) {
        this.userHeadPic = userHeadPic;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public String getUserBirth() {
        return userBirth;
    }

    public void setUserBirth(String userBirth) {
        this.userBirth = userBirth;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }
}
