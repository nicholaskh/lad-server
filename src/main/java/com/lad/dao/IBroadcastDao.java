package com.lad.dao;

import com.lad.scrapybo.BroadcastBo;
import com.mongodb.WriteResult;

import java.util.HashSet;
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


    List<BroadcastBo> findByClassName(String groupName, String className);


    List<BroadcastBo> findByClassNamePage(String groupName, String className, int start, int end);


    WriteResult updateRadioNum(String radioid, int type, int num);


    List<BroadcastBo> findRadioByIds(List<String> radioIds);

    List<BroadcastBo> selectClassByGroups(HashSet<String> modules, HashSet<String> classNames);


    List<BroadcastBo> findByLimit(HashSet<String> modules, HashSet<String> classNames, int limit);

}
