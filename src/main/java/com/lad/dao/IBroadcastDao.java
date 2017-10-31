package com.lad.dao;

import com.lad.scrapybo.BroadcastBo;

import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/29
 */
public interface IBroadcastDao {

    
    BroadcastBo findById(String id);


    List<BroadcastBo> selectGroups();

    List<BroadcastBo> selectClassByGroups(String groupName);

    List<BroadcastBo> findByPage(String groupName, int page, int limit);


    List<BroadcastBo> findByLimit(int limit);


}
