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

    private String noteid;


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
}
