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

    List<SearchBo> findByTimes(int type);

}