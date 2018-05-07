package com.lad.bo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 功能描述：圈子分类/个人兴趣分类
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/8/21
 */
@Getter
@Setter
@Document(collection = "circleType")
public class CircleTypeBo extends BaseBo {

    /**
     * 圈子
     */
    public static final int CIRCLE = 0;
    /**
     * 个人兴趣
     */
    public static final int USER_TASTE = 1;
    /**
     * 演出分类，演出无一二级分类
     */
    public static final int SHOW_TYPE = 2;


    //分类名称
    private String category;
    
    //父分类名称
    private String preCateg;
    //1 一级分类， 2 二级分类
    private int level;
    
    //0，圈子分类； 1 个人兴趣分类  2 演出类型分类
    private int type;

    //次数统计
    private int times;

}
