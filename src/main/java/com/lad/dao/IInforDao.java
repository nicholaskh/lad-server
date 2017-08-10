package com.lad.dao;

import com.lad.scrapybo.InforBo;

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


}