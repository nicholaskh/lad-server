package com.lad.dao.impl;

import com.lad.bo.InforUserReadHisBo;
import com.lad.dao.IInforUserReadHisDao;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/10/25
 */
@Repository("inforUserReadHisDao")
public class InforUserReadHisDaoImpl implements IInforUserReadHisDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public InforUserReadHisBo addUserReadHis(InforUserReadHisBo userReadHisBo) {
        mongoTemplate.insert(userReadHisBo);
        return userReadHisBo;
    }

    @Override
    public InforUserReadHisBo findUserReadHis(String userid, int type, String module, Date halfTime) {
        Query query = new Query();
        query.addCriteria(new Criteria("userid").is(userid));
        query.addCriteria(new Criteria("type").is(type));
        query.addCriteria(new Criteria("module").is(module));
        query.addCriteria(new Criteria("lastDate").gt(halfTime));
        return mongoTemplate.findOne(query, InforUserReadHisBo.class);
    }

    @Override
    public WriteResult updateUserReadHis(String id, Date currentDate) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.set("lastDate", currentDate);
        return mongoTemplate.updateFirst(query, update, InforUserReadHisBo.class);
    }
}
