package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 功能描述：评论
 * Version: 1.0
 * Time:2017/6/25
 */
@Document(collection="comment")
public class CommentBo extends BaseBo{

    private String content;

    private String parentid;

    private String userName;
    //帖子ID
    private String noteid;

    //评论的 目标ID，根据评论类型而定，note为noteid，不算在里面
    private String targetid;

    //评论的类型 0 帖子评论； 1 资讯评论
    private int type;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getNoteid() {
        return noteid;
    }

    public void setNoteid(String noteid) {
        this.noteid = noteid;
    }

    public String getTargetid() {
        return targetid;
    }

    public void setTargetid(String targetid) {
        this.targetid = targetid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
