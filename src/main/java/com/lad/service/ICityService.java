package com.lad.service;

import com.lad.bo.CityBo;
import com.mongodb.BasicDBObject;

import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/6
 */
public interface ICityService {
    /**
     * 查找所有城市信息
     * @return
     */
    List<CityBo> findAllCitys();
    /**
     * 根据省  市  区查询
     * @param province
     * @param city
     * @param distrit
     * @return
     */
    List<CityBo> findByParams(String province, String city, String distrit);
    /**
     * 直辖市查询
     * @param province
     * @param distrit
     * @return
     */
    List<CityBo> findByParams(String province, String distrit);
    /**
     * 查找所有省市信息
     * @return
     */
    List<BasicDBObject> findProvince();

    /**
     * 查找省下城市
     * @param province
     * @return
     */
    List<BasicDBObject> findCitys(String province);
}
