package com.lad.dao.impl;

import com.lad.bo.DynamicBo;
import com.lad.dao.IDynamicDao;
import com.lad.util.Constant;
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
 * Time:2017/9/23
 */
@Repository("dynamicDao")
public class DynamicDaoImpl implements IDynamicDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public DynamicBo insert(DynamicBo dynamicBo) {
        mongoTemplate.insert(dynamicBo);
        return dynamicBo;
    }

    @Override
    public WriteResult delete(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        Update update = new Update();
        update.set("deleted",Constant.DELETED);
        return mongoTemplate.updateFirst(query, update, DynamicBo.class);
    }

    @Override
    public DynamicBo findById(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        return mongoTemplate.findOne(query, DynamicBo.class);
    }

    @Override
    public WriteResult update(String id, int num, int type) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        Update update = new Update();
        switch (type) {
            case Constant.ONE:
                update.set("transNum", num);
            case Constant.TWO:
                update.set("commentNum", num);
            case Constant.THREE:
                update.set("thumpNum", num);
            default:
                break;
        }
        return mongoTemplate.updateFirst(query, update, DynamicBo.class);
    }
}
