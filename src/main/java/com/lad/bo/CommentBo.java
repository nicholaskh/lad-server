package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedHashSet;

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

    //评论的类型 
    private int type;
    //子分类类型，如资讯健康评论，资讯安全评论等
    private int subType;

    //贴子的发帖人
    private String ownerid;

    private LinkedHashSet<String> photos;

    private int thumpsubNum;


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

    public String getOwnerid() {
        return ownerid;
    }

    public void setOwnerid(String ownerid) {
        this.ownerid = ownerid;
    }

    public LinkedHashSet<String> getPhotos() {
        return photos;
    }
    public void setPhotos(LinkedHashSet<String> photos) {
        this.photos = photos;
    }

    public int getThumpsubNum() {
        return thumpsubNum;
    }

    public void setThumpsubNum(int thumpsubNum) {
        this.thumpsubNum = thumpsubNum;
    }

    public int getSubType() {
        return subType;
    }

    public void setSubType(int subType) {
        this.subType = subType;
    }
}
