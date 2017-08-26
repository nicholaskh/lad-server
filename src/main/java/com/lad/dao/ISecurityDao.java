package com.lad.dao;

import com.lad.scrapybo.SecurityBo;

import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/8/26
 */
public interface ISecurityDao {


    List<SecurityBo> findAllTypes();

    List<SecurityBo> findByCity(String cityName, String createTime, int limit);

    List<SecurityBo> findByType(String typeName, String createTime, int limit);

    SecurityBo findById(String id);

}
