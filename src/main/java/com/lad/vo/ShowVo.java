package com.lad.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2018
 * Version: 1.0
 * Time:2018/4/26
 */
@Setter
@Getter
@ToString
@ApiModel(value="showVo",description="演出对象")
public class ShowVo implements Serializable {

    @ApiModelProperty(value="演出id")
    private String showid;

    @ApiModelProperty(value="1招演出，2接演出",name="type")
    private int type;

    @ApiModelProperty(value="演出时间,格式yyyy-MM-dd HH:mm:ss")
    private String showTime;

    @ApiModelProperty(value="演出金额")
    private String showAmount;

    @ApiModelProperty(value="演出类型")
    private String showType;

    @ApiModelProperty(value="演出地点省份")
    private String province;

    @ApiModelProperty(value="演出地点城市")
    private String city;

    @ApiModelProperty(value="招演出的详细地址")
    private String address;

    @ApiModelProperty(value="详细信息状态")
    private int status;

    @ApiModelProperty(value="演出联系电话")
    private String phone;

    @ApiModelProperty(value="演出联系人")
    private String contact;

    @ApiModelProperty(value="演出简介或者招演出要求")
    private String brief;

    @ApiModelProperty(value="招演公司或者演出团队名称")
    private String company;

    @ApiModelProperty(value="招演公司营业执照图片")
    private String comPic;

    @ApiModelProperty(value="接演团队图片或者视频类型")
    private String picType;

    @ApiModelProperty(value="接演团队图片")
    private LinkedHashSet<String> images;

    @ApiModelProperty(value="接演团队视频")
    private String video;

    @ApiModelProperty(value="接演团队视频类缩略图")
    private String videoPic;

    @ApiModelProperty(value="接演归属圈子")
    private String circleid;

    @ApiModelProperty(value="招接演出发布人")
    private UserBaseVo creatUser;

    @ApiModelProperty(value="发布时间")
    private Date createTime;

    @ApiModelProperty(value="修改时间")
    private Date updateTime;

    @ApiModelProperty(value="是否是本人发布的信息")
    private boolean isCreate;
}
