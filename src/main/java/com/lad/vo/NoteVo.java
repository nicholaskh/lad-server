package com.lad.vo;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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

    public boolean isMyThumbsup() {
        return isMyThumbsup;
    }

    public boolean isCollect() {
        return isCollect;
    }

    public void setCollect(boolean collect) {
        isCollect = collect;
    }

    public String getFromUserid() {
        return fromUserid;
    }

    public void setFromUserid(String fromUserid) {
        this.fromUserid = fromUserid;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getFromUserPic() {
        return fromUserPic;
    }

    public void setFromUserPic(String fromUserPic) {
        this.fromUserPic = fromUserPic;
    }

    public String getFromUserSign() {
        return fromUserSign;
    }

    public void setFromUserSign(String fromUserSign) {
        this.fromUserSign = fromUserSign;
    }

    public String getFromUserSex() {
        return fromUserSex;
    }

    public void setFromUserSex(String fromUserSex) {
        this.fromUserSex = fromUserSex;
    }

    public int getForwardType() {
        return forwardType;
    }

    public void setForwardType(int forwardType) {
        this.forwardType = forwardType;
    }

    public int getInforType() {
        return inforType;
    }

    public void setInforType(int inforType) {
        this.inforType = inforType;
    }

    public String getInforTypeName() {
        return inforTypeName;
    }

    public void setInforTypeName(String inforTypeName) {
        this.inforTypeName = inforTypeName;
    }

    public String getInforUrl() {
        return inforUrl;
    }

    public void setInforUrl(String inforUrl) {
        this.inforUrl = inforUrl;
    }

    public String getSourceid() {
        return sourceid;
    }

    public void setSourceid(String sourceid) {
        this.sourceid = sourceid;
    }

    public List<UserNoteVo> getAtUsers() {
        return atUsers;
    }

    public void setAtUsers(List<UserNoteVo> atUsers) {
        this.atUsers = atUsers;
    }

    public int getFromUserLevel() {
        return fromUserLevel;
    }

    public void setFromUserLevel(int fromUserLevel) {
        this.fromUserLevel = fromUserLevel;
    }

    public String getFromUserBirth() {
        return fromUserBirth;
    }

    public void setFromUserBirth(String fromUserBirth) {
        this.fromUserBirth = fromUserBirth;
    }
}
