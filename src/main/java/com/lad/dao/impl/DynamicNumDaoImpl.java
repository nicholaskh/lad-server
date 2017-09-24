package com.lad.dao.impl;

import com.lad.bo.DynamicNumBo;
import com.lad.dao.IDynamicNumDao;
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
 * Time:2017/9/24
 */
@Repository("dynamicNumDao")
public class DynamicNumDaoImpl implements IDynamicNumDao {

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public DynamicNumBo addNum(DynamicNumBo numBo) {
        mongoTemplate.insert(numBo);
        return numBo;
    }

    @Override
    public DynamicNumBo findByUserid(String userid) {
        Query query = new Query();
        query.addCriteria(new Criteria("userid").is(userid));
        return mongoTemplate.findOne(query, DynamicNumBo.class);
    }

    @Override
    public WriteResult updateNumbers(String id, int addNum) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.inc("number", addNum);
        return mongoTemplate.updateFirst(query, update, DynamicNumBo.class);
    }
}
