package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 功能描述：红人计量表
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/6/29
 */
@Document(collection = "redstar")
public class RedstarBo extends BaseBo {

    private String circleid;

    private String userid;

    //红人总榜评论数
    private Long commentTotal;

    //红人周榜评论数
    private Long commentWeek;

    //最后更新日期是今年第几周
    private int weekNo;

    //最后更新日期是今年第几周
    private int year;

    public String getCircleid() {
        return circleid;
    }

    public void setCircleid(String circleid) {
        this.circleid = circleid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Long getCommentTotal() {
        return commentTotal;
    }

    public void setCommentTotal(Long commentTotal) {
        this.commentTotal = commentTotal;
    }

    public Long getCommentWeek() {
        return commentWeek;
    }

    public void setCommentWeek(Long commentWeek) {
        this.commentWeek = commentWeek;
    }

    public int getWeekNo() {
        return weekNo;
    }

    public void setWeekNo(int weekNo) {
        this.weekNo = weekNo;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
