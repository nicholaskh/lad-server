package com.lad.service.impl;

import com.lad.bo.SearchBo;
import com.lad.dao.ISearchDao;
import com.lad.service.ISearchService;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/4
 */
@Service("searchService")
public class SearchService implements ISearchService {

    @Autowired
    private ISearchDao searchDao;

    @Override
    public SearchBo insert(SearchBo searchBo) {
        return searchDao.insert(searchBo);
    }

    @Override
    public SearchBo findByKeyword(String keyword, int type) {
        return searchDao.findByKeyword(keyword, type);
    }

    @Override
    public WriteResult update(String id) {
        return searchDao.update(id);
    }

    @Override
    public WriteResult delete(String id) {
        return searchDao.delete(id);
    }

    @Override
    public List<SearchBo> findByTimes(int type, int limit) {
        return searchDao.findByTimes(type, limit);
    }

    @Override
    public List<SearchBo> findInforByTimes(int type, int inforType, int limit) {
        return searchDao.findInforByTimes(type, inforType, limit);
    }

    @Override
    public SearchBo findInforByKeyword(String keyword, int type, int inforType) {
        return searchDao.findInforByKeyword(keyword, type, inforType);
    }
}
