package com.lad.bo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * 功能描述：城市实体类
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/5
 */
@Document(collection = "city")
public class CityBo implements Serializable {
    
    @Id
    private String id;

    private String province;

    private String city;

    private String distrit;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrit() {
        return distrit;
    }

    public void setDistrit(String distrit) {
        this.distrit = distrit;
    }
}
