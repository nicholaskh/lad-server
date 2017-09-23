package com.lad.dao.impl;

import com.lad.bo.DynamicBackBo;
import com.lad.dao.IDynamicBackDao;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/23
 */
@Repository("dynamicBackDao")
public class DynamicBackDaoImpl implements IDynamicBackDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public DynamicBackBo insert(DynamicBackBo backBo) {
        mongoTemplate.insert(backBo);
        return backBo;
    }

    @Override
    public DynamicBackBo findByUserid(String userid) {
        Query query = new Query();
        query.addCriteria(new Criteria("userid").is(userid));
        return mongoTemplate.findOne(query, DynamicBackBo.class);
    }

    @Override
    public WriteResult updateNotSee(String id, HashSet<String> notSeeBacks) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.set("notSeeBacks", notSeeBacks);
        return mongoTemplate.updateFirst(query, update, DynamicBackBo.class);
    }

    @Override
    public WriteResult updateNotAllow(String id, HashSet<String> notAllowBacks) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.set("notAllowBacks", notAllowBacks);
        return mongoTemplate.updateFirst(query, update, DynamicBackBo.class);
    }

    @Override
    public List<DynamicBackBo> findWhoBackMe(String userid) {
        Query query = new Query();
        query.addCriteria(new Criteria("notAllowBacks").in(userid));
        return mongoTemplate.find(query, DynamicBackBo.class);
    }
}
