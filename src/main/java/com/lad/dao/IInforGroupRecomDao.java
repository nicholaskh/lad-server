package com.lad.dao;

import com.lad.bo.InforGroupRecomBo;
import com.mongodb.WriteResult;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/12/10
 */
public interface IInforGroupRecomDao {


    /**
     * 添加
     * @param groupRecomBo
     * @return
     */
    InforGroupRecomBo addInforGroup(InforGroupRecomBo groupRecomBo);

    /**
     *
     * @param module
     * @param className
     * @param type
     * @return
     */
    InforGroupRecomBo findInforGroup(String module, String className, int type);


    /**
     *
     * @param module
     * @param type
     * @return
     */
    InforGroupRecomBo findInforGroup(String module, int type);

    /**
     * 根据类型和分类
     * @param type
     * @param modules
     * @return
     */
    List<InforGroupRecomBo> findInforGroupByModule(int type, LinkedHashSet<String> modules);

    /**
     * 根据类型和分类
     * @param type
     * @return
     */
    List<InforGroupRecomBo> findInforGroupWithoutModule(int type, LinkedHashSet<String> modules, int limit);

    /**
     * 更新热度信息
     * @param id
     * @param halfNum
     * @param totalNum
     * @return
     */
    WriteResult updateInforGroup(String id, int halfNum, int totalNum);



}
