package com.lad.dao;

import com.lad.scrapybo.InforBo;
import com.lad.scrapybo.SecurityBo;
import com.mongodb.WriteResult;

import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/8/1
 */
public interface IInforDao {


    List<InforBo> selectAllInfos();

    List<InforBo> findGroups(String module);

    List<InforBo> findByList(String groupName, String createTime, int limit);

    InforBo findById(String id);

    /**
     * 首页推荐
     * @return
     */
    List<InforBo> homeHealthRecom(int limit);

    /**
     * 首页推荐
     * @return
     */
    List<InforBo> userHealthRecom(String userid, int limit);


    List<InforBo> findHealthByIds(List<String> healthIds);

    /**
     * 
     * @param inforid
     * @param type
     * @param num
     * @return
     */
    WriteResult updateInforNum(String inforid, int type, int num);


    /**
     * 根据名称匹配
     * @param title
     * @param page
     * @param limit
     * @return
     */
    List<InforBo> findByTitleRegex(String title, int page, int limit);

}
