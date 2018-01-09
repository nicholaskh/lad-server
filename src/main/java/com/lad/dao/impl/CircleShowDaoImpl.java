package com.lad.dao.impl;

import com.lad.bo.CircleShowBo;
import com.lad.dao.ICircleShowDao;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2018
 * Version: 1.0
 * Time:2018/1/9
 */
@Repository("circleShowDao")
public class CircleShowDaoImpl implements ICircleShowDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public CircleShowBo addCircleShow(CircleShowBo showBo) {
        mongoTemplate.insert(showBo);
        return showBo;
    }

    @Override
    public List<CircleShowBo> findCircleShows(String circleid, int page, int limit) {
        Query query = new Query();
        query.addCriteria(new Criteria("circleid").is(circleid));
        query.with(new Sort(Sort.Direction.DESC, "createTime"));
        page = page < 1 ? 1 : page;
        query.skip((page -1)*limit);
        query.limit(limit);
        return mongoTemplate.find(query, CircleShowBo.class);
    }

    @Override
    public WriteResult deleteShow(String targetid) {
        Query query = new Query();
        query.addCriteria(new Criteria("targetid").is(targetid));
        return mongoTemplate.remove(query, CircleShowBo.class);
    }
}
