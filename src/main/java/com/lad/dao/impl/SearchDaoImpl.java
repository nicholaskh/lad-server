package com.lad.dao.impl;

import com.lad.bo.SearchBo;
import com.lad.dao.ISearchDao;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/4
 */
@Repository("searchDao")
public class SearchDaoImpl implements ISearchDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public SearchBo insert(SearchBo searchBo) {
        mongoTemplate.insert(searchBo);
        return searchBo;
    }

    @Override
    public SearchBo findByKeyword(String keyword, int type) {
        Query query = new Query();
        query.addCriteria(new Criteria("keyword").is(keyword));
        query.addCriteria(new Criteria("type").is(type));
        return mongoTemplate.findOne(query,SearchBo.class);
    }

    @Override
    public WriteResult update(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.inc("times", 1);
        return mongoTemplate.updateFirst(query, update, SearchBo.class);
    }

    @Override
    public WriteResult delete(String id) {
        return null;
    }

    @Override
    public List<SearchBo> findByTimes(int type, int limit) {
        Query query = new Query();
        //圈子
        query.addCriteria(new Criteria("type").is(type));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "times")));
        query.limit(limit);
        return mongoTemplate.find(query, SearchBo.class);
    }

    @Override
    public List<SearchBo> findInforByTimes(int type, int inforType, int limit) {
        Query query = new Query();
        //圈子
        query.addCriteria(new Criteria("type").is(type).and("inforType").is(inforType));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "times")));
        query.limit(limit);
        return mongoTemplate.find(query, SearchBo.class);

    }

    @Override
    public SearchBo findInforByKeyword(String keyword, int type, int inforType) {
        Query query = new Query();
        query.addCriteria(new Criteria("keyword").is(keyword).and("type").is(type).and("inforType").is(inforType));
        return mongoTemplate.findOne(query,SearchBo.class);
    }
}
