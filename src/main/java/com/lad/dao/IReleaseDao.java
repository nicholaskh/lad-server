package com.lad.dao;

import com.lad.bo.ReleaseBo;
import com.mongodb.WriteResult;

import java.util.List;
import java.util.Map;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2018
 * Version: 1.0
 * Time:2018/4/13
 */
public interface IReleaseDao {

    /**
     * 创建
     * @param releaseBo
     * @return
     */
    ReleaseBo insert(ReleaseBo releaseBo);

    /**
     * 根据id查找
     * @param id
     * @return
     */
    ReleaseBo findById(String id);

    /**
     * 根据类型查找
     * @param releaseType
     * @return
     */
    List<ReleaseBo> findByType(int releaseType, int page, int limit);

    /**
     * 根据条件相等查找
     * @param releaseType
     * @return
     */
    List<ReleaseBo> findByParamsType(Map<String, Object> params, int releaseType, int page, int limit);

    /**
     * 修改信息
     * @param id
     * @param params
     * @return
     */
    WriteResult updateByParams(String id, Map<String, Object> params);

    /**
     * 删除发布信息
     * @param id
     * @return
     */
    WriteResult delete(String id);

    /**
     * 根据年龄区间查找
     * @param startAge
     * @param endAge
     * @param releaseType
     * @param page
     * @param limit
     * @return
     */
    List<ReleaseBo> findByAgeWave(int startAge, int endAge, int releaseType, int page, int limit);


    /**
     * 根据工资区间查找
     * @param start
     * @param end
     * @param releaseType
     * @param page
     * @param limit
     * @return
     */
    List<ReleaseBo> findByWageWave(int start, int end, int releaseType, int page, int limit);


    /**
     * 根据旅行目的地查找
     * @param addrs
     * @param releaseType
     * @param page
     * @param limit
     * @return
     */
    List<ReleaseBo> findTourAddrs(String addrs, int releaseType, int page, int limit);

    /**
     * 根据演出类型查找地查找
     * @param types
     * @param releaseType
     * @param page
     * @param limit
     * @return
     */
    List<ReleaseBo> findByShowTyps(String types, int releaseType, int page, int limit);

}
