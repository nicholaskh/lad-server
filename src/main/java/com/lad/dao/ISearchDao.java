package com.lad.dao;

import com.lad.bo.SearchBo;
import com.mongodb.WriteResult;

import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/4
 */
public interface ISearchDao {

    SearchBo insert(SearchBo searchBo);

    SearchBo findByKeyword(String keyword, int type);

    WriteResult update(String id);

    WriteResult delete(String id);

    List<SearchBo> findByTimes(int type, int limit);

    /**
     * 资讯搜索历史
     * @param type
     * @param inforType
     * @param limit
     * @return
     */
    List<SearchBo> findInforByTimes(int type, int inforType, int limit);

    /**
     * 资讯搜索
     * @param keyword
     * @param type
     * @param inforType
     * @return
     */
    SearchBo findInforByKeyword(String keyword, int type, int inforType);

}
