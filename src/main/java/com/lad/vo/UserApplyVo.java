package com.lad.vo;

/**
 * 功能描述：
 * Version: 1.0
 * Time:2017/7/5
 */
public class UserApplyVo extends BaseVo {

    private String userid;

    private String userName;

    private String sex;

    private String headPictureName;

    private String birthDay;

    private String circleid;

    private int status;

    private String reason;

    private String refuse;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getCircleid() {
        return circleid;
    }

    public void setCircleid(String circleid) {
        this.circleid = circleid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRefuse() {
        return refuse;
    }

    public void setRefuse(String refuse) {
        this.refuse = refuse;
    }
}
