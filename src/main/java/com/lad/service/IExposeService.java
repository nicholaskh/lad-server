package com.lad.service;

import com.lad.bo.ExposeBo;
import com.mongodb.WriteResult;

import java.util.List;
import java.util.Map;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2018
 * Version: 1.0
 * Time:2018/4/22
 */
public interface IExposeService {

    /**
     * 插入
     * @param exposeBo
     * @return
     */
    ExposeBo insert(ExposeBo exposeBo);

    /**
     * 根据id修改
     * @param id
     * @param params
     * @return
     */
    WriteResult updateExpose(String id, Map<String, Object> params);

    /**
     * 根据标题或类型模糊查询
     * @param title
     * @param exposeTypes
     * @param page
     * @param limit
     * @return
     */
    List<ExposeBo> findByRegex(String title, List<String> exposeTypes, int page, int limit);


    /**
     * 根据id查找
     * @param id
     * @return
     */
    ExposeBo findById(String id);


    /**
     * 根据id删除
     * @param id
     * @return
     */
    WriteResult deleteById(String id);


    /**
     * 根据参数查找
     * @param params
     * @param page
     * @param limit
     * @return
     */
    List<ExposeBo> findByParams(Map<String, Object> params, int page, int limit);


    /**
     * 更新阅读点赞等数据
     * @param id
     * @param numType
     * @param num
     * @return
     */
    WriteResult updateCounts(String id, int numType, int num);

	void updateVisitNum(String exposeid, int i);
}
