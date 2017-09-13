package com.lad.bo;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 功能描述：圈子分类/个人兴趣分类
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/8/21
 */
@Document(collection = "circleType")
public class CircleTypeBo extends BaseBo {
    //分类名称
    private String category;
    
    //父分类名称
    private String preCateg;
    //1 一级分类， 2 二级分类
    private int level;
    
    //0，圈子分类； 1 个人兴趣分类
    private int type;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPreCateg() {
        return preCateg;
    }

    public void setPreCateg(String preCateg) {
        this.preCateg = preCateg;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
