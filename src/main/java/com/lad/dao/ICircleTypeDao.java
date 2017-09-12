package com.lad.dao;

import com.lad.bo.CircleTypeBo;

import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/8/21
 */
public interface ICircleTypeDao {

    CircleTypeBo insert(CircleTypeBo circleTypeBo);

    List<CircleTypeBo> selectByParent(String preCateg, int type);

    CircleTypeBo selectByNameLevel(String name, int level, int type);

    List<CircleTypeBo> findAll(int start, int limit, int type);

    List<CircleTypeBo> selectByLevel(int level, int type);

    List<CircleTypeBo> findAll();

}
