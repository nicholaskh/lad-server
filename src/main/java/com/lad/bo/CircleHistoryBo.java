package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.ToString;

/**
 * 功能描述：个人的圈子操作记录
 * Version: 1.0
 * Time:2017/8/16
 */
@Document(collection = "circleHistory")
@ToString
public class CircleHistoryBo extends BaseBo {
	//访问用户id
    private String userid;
    //圈子id
    private String circleid;
    //圈子地址
    private double[] position;
    //访问记录类型， 0圈子访问， 1 圈子操作
    private int type;
    //操作标题
    private String title;
    //操作内容
    private String content;
    //操作用户id
    private String operateid;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getCircleid() {
        return circleid;
    }

    public void setCircleid(String circleid) {
        this.circleid = circleid;
    }

    public double[] getPosition() {
        return position;
    }

    public void setPosition(double[] position) {
        this.position = position;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

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

    public String getOperateid() {
        return operateid;
    }

    public void setOperateid(String operateid) {
        this.operateid = operateid;
    }
}
