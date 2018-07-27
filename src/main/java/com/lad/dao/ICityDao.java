package com.lad.dao;

import com.lad.bo.CityBo;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;

import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/5
 */
public interface ICityDao {

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

    CityBo insert(CityBo cityBo);
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

    List<String> getProvince();

	List<String> getCity(String provice);

	List<String> getDistrit(String city);
}
