package com.lad.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/10/2
 */
@Getter
@Setter
public class PartyListVo extends BaseVo {

    private String partyid;

    private String title;
    //聚会背景图片
    private String backPic;

    private LinkedHashSet<String> photos;

    private String video;
    //视频缩略图
    private String videoPic;
    //聚会时间
    private LinkedHashSet<String> startTime;
    //具体地点，线上必填
    private String addrInfo;
    //线下地标
    private String landmark;
    //聚会状态 0 已发起，1 进行中， 2 报名结束 ，3 活动结束 5 已取消
    private int status;

    private int userNum;

    private boolean hasNotice;

    private boolean isForward;

    private String fromUserid;

    private String fromUserName;

    private String fromUserPic;

    private String fromUserSign;

    private String fromUserSex;

    private int fromUserLevel;

    private String sourceCirid;

    private String sourceCirName;

    private String fromUserBirth;

    private String view;

    private double distance;

    private boolean isJoin;
}
