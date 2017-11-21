package com.lad.vo;

import java.util.Date;
import java.util.LinkedList;

/**
 * 功能描述：
 * Version: 1.0
 * Time:2017/6/25
 */
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

    private boolean isForward = false;

    public String getNodeid() {
        return nodeid;
    }

    public void setNodeid(String nodeid) {
        this.nodeid = nodeid;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(Long visitCount) {
        this.visitCount = visitCount;
    }

    public Long getCommontCount() {
        return commontCount;
    }

    public void setCommontCount(Long commontCount) {
        this.commontCount = commontCount;
    }

    public Long getTransCount() {
        return transCount;
    }

    public void setTransCount(Long transCount) {
        this.transCount = transCount;
    }

    public String getCreateuid() {
        return createuid;
    }

    public void setCreateuid(String createuid) {
        this.createuid = createuid;
    }

    public boolean isForward() {
        return isForward;
    }

    public void setForward(boolean forward) {
        isForward = forward;
    }

    public LinkedList<String> getPhotos() {
        return photos;
    }

    public void setPhotos(LinkedList<String> photos) {
        this.photos = photos;
    }

    public double[] getPosition() {
        return position;
    }

    public void setPosition(double[] position) {
        this.position = position;
    }

    public String getCircleId() {
        return circleId;
    }

    public void setCircleId(String circleId) {
        this.circleId = circleId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getHeadPictureName() {
        return headPictureName;
    }

    public void setHeadPictureName(String headPictureName) {
        this.headPictureName = headPictureName;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public Long getThumpsubCount() {
        return thumpsubCount;
    }

    public void setThumpsubCount(Long thumpsubCount) {
        this.thumpsubCount = thumpsubCount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getEssence() {
        return essence;
    }

    public void setEssence(int essence) {
        this.essence = essence;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public String getCirName() {
        return cirName;
    }

    public void setCirName(String cirName) {
        this.cirName = cirName;
    }

    public String getCirHeadPic() {
        return cirHeadPic;
    }

    public void setCirHeadPic(String cirHeadPic) {
        this.cirHeadPic = cirHeadPic;
    }

    public int getCirNoteNum() {
        return cirNoteNum;
    }

    public void setCirNoteNum(int cirNoteNum) {
        this.cirNoteNum = cirNoteNum;
    }

    public String getVideoPic() {
        return videoPic;
    }

    public void setVideoPic(String videoPic) {
        this.videoPic = videoPic;
    }

    public int getCirVisitNum() {
        return cirVisitNum;
    }

    public void setCirVisitNum(int cirVisitNum) {
        this.cirVisitNum = cirVisitNum;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }
}
