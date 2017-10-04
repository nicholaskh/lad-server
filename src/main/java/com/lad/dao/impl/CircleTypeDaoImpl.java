package com.lad.dao.impl;

import com.lad.bo.CircleTypeBo;
import com.lad.dao.ICircleTypeDao;
import com.lad.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/8/21
 */
@Repository("circleTypeDao")
public class CircleTypeDaoImpl implements ICircleTypeDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public CircleTypeBo insert(CircleTypeBo circleTypeBo) {
        mongoTemplate.insert(circleTypeBo);
        return circleTypeBo;
    }

    public List<CircleTypeBo> selectByParent(String preCateg, int type) {
        Query query = new Query();
        query.addCriteria(new Criteria("preCateg").is(preCateg));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        query.addCriteria(new Criteria("type").is(type));
        return mongoTemplate.find(query, CircleTypeBo.class);
    }

    public CircleTypeBo selectByNameLevel(String name, int level, int type) {
        Query query = new Query();
        query.addCriteria(new Criteria("category").is(name));
        query.addCriteria(new Criteria("level").is(level));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        query.addCriteria(new Criteria("type").is(type));
        return mongoTemplate.findOne(query, CircleTypeBo.class);
    }

    public List<CircleTypeBo> selectByLevel(int level, int type) {
        Query query = new Query();
        query.addCriteria(new Criteria("level").is(level));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        query.addCriteria(new Criteria("type").is(type));
        return mongoTemplate.find(query, CircleTypeBo.class);
    }

    @Override
    public List<CircleTypeBo> findAll(int start, int limit, int type) {
        Query query = new Query();
        query.addCriteria(new Criteria("deleted").is(0));
        query.addCriteria(new Criteria("type").is(type));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
        query.skip(start);
        query.limit(limit);
        return mongoTemplate.find(query, CircleTypeBo.class);
    }

    public List<CircleTypeBo> findAll() {
        return mongoTemplate.findAll(CircleTypeBo.class);
    }

    @Override
    public CircleTypeBo findEsixtTagName(String keyword, int type) {
        Query query = new Query();
        query.addCriteria(new Criteria("category").is(keyword));
        query.addCriteria(new Criteria("type").is(type));
        return mongoTemplate.findOne(query, CircleTypeBo.class);
    }

}
