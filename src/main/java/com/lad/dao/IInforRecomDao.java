package com.lad.dao;

import com.lad.bo.InforRecomBo;
import com.mongodb.WriteResult;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/10/24
 */
public interface IInforRecomDao {
    
    InforRecomBo addInforRecom(InforRecomBo recomBo);

    /**
     * 更具资讯id查询
     * @param inforid
     * @return
     */
    InforRecomBo findRecomByInforid(String inforid);

    /**
     * 更新热度信息
     * @param id
     * @param halfNum
     * @param totalNum
     * @return
     */
    WriteResult updateRecomByInforid(String id, int halfNum, int totalNum);

    /**
     * 根据类型和分类
     * @param type
     * @param modules
     * @return
     */
    List<InforRecomBo> findRecomByTypeAndModule(int type, LinkedHashSet<String> modules);

    /**
     * 根据类型的前50
     * @param type
     * @return
     */
    List<InforRecomBo> findRecomByType(int type, int limit);
    
}
