package com.lad.dao;

import com.lad.scrapybo.BroadcastBo;
import com.lad.scrapybo.SecurityBo;
import com.mongodb.WriteResult;

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

    //查找制定条数
    List<SecurityBo> findByLimiy(int limit);

    /**
     * 更新各组访问量
     * @param inforid
     * @param type
     * @param num
     * @return
     */
    WriteResult updateSecurityNum(String inforid, int type, int num);

    /**
     * 根据id批量查找
     * @param SecurityIds
     * @return
     */
    List<SecurityBo> findSecurityByIds(List<String> SecurityIds);

    /**
     * 根据名称匹配
     * @param title
     * @param page
     * @param limit
     * @return
     */
    List<SecurityBo> findByTitleRegex(String title, int page, int limit);
}
