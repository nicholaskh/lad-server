package com.lad.bo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedHashSet;

/**
 * 功能描述： 演出类型
 * Copyright: Copyright (c) 2018
 * Version: 1.0
 * Time:2018/4/24
 */
@Getter
@Setter
@Document(collection = "show")
public class ShowBo extends BaseBo {
    /**
     * 招演出
     */
    public static final int NEED = 1;
    /**
     * 接演出
     */
    public static final int PROVIDE = 2;
    
    private int type;

    private String showTime;

    private String showAmount;

    private String showType;

    private String province;

    private String city;
    //区或者县
    private String dist;
    //详细地址
    private String address;
    //演出信息的状态  0 发布， 1结束
    private int status;

    private String phone;

    private String contact;

    private String brief;
    //招演公司或者演出团队
    private String company;
    //招演公司营业执照图片
    private String comPic;

    private String picType;

    private LinkedHashSet<String> images;

    private String video;

    private String videoPic;
    //演出圈子
    private String circleid;

}
