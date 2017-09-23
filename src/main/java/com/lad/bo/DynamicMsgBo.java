package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/20
 */
@Document(collection = "dynamicMsg")
public class DynamicMsgBo extends BaseBo {

    //帖子、聚会、动态的id
    private String targetid;

    //1 帖子、2 聚会、3 动态类型
    private int dynamicType;

    private String userid;

    public String getTargetid() {
        return targetid;
    }

    public void setTargetid(String targetid) {
        this.targetid = targetid;
    }

    public void setDynamicType(int dynamicType) {
        this.dynamicType = dynamicType;
    }

    public int getDynamicType() {
        return dynamicType;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

}
