package com.lad.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 功能描述：
 * Version: 1.0
 * Time:2017/6/25
 */
@Getter
@Setter
public class NoteVo extends BaseVo {

    private String username;

    private String sex;

    private String headPictureName;

    private String birthDay;

    private int userLevel;

    private String nodeid;

    private String subject;

    private String content;

    private Long visitCount;

    private Long thumpsubCount;

    private Long commontCount;

    private Long transCount;

    private boolean isMyThumbsup;

    private String type;

    //精华  管理员操作
    private int essence;
    //置顶  管理员操作
    private int top;

    //圈子名字
    private String cirName;

    ///圈子头像
    private String cirHeadPic;

    ///当前帖子所在圈子的帖子数量
    private int cirNoteNum;
    ///当前帖子所在圈子的阅读数量
    private int cirVisitNum;
    //视频缩略图
    private String videoPic;
    //是否收藏
    private boolean isCollect;

    private String fromUserid;

    private String fromUserName;

    private String fromUserPic;

    private String fromUserSign;

    private String fromUserSex;

    private int fromUserLevel;

    private String fromUserBirth;

    public boolean getMyThumbsup() {
        return isMyThumbsup;
    }

    public void setMyThumbsup(boolean myThumbsup) {
        isMyThumbsup = myThumbsup;
    }

    private LinkedList<String> photos = new LinkedList<>();

    private Date createTime;                        

    private String createuid;

    private double[] position;
    private String circleId;

    private String landmark;
    //原信息id
    private String sourceid;

    private boolean isForward = false;
    //转发的来源类型，0 表示帖子，1 表示来源是资讯
    private int forwardType;

    private int inforType;

    private String inforTypeName;
    //视频或者广播的url
    private String inforUrl;

    private List<UserNoteVo> atUsers;

    private double distance;
}
