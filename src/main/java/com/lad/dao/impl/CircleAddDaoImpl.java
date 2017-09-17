package com.lad.dao.impl;

import com.lad.bo.CircleAddBo;
import com.lad.dao.ICircleAddDao;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/17
 */
@Repository("circleAddDao")
public class CircleAddDaoImpl implements ICircleAddDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public CircleAddBo insert(CircleAddBo addBo) {
        mongoTemplate.insert(addBo);
        return addBo;
    }

    @Override
    public CircleAddBo findByUserAndCircle(String userid, String circleid) {

        Query query = new Query();
        query.addCriteria(new Criteria("userid").is(userid));
        query.addCriteria(new Criteria("circleid").is(circleid));
        return mongoTemplate.findOne(query, CircleAddBo.class);
    }

    @Override
    public WriteResult updateJoinStatus(String id, int status) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.set("status", status);
        return mongoTemplate.updateFirst(query, update, CircleAddBo.class);
    }
}
