package com.lad.dao.impl;

import com.lad.bo.InforUserReadHisBo;
import com.lad.dao.IInforUserReadHisDao;
import com.mongodb.WriteResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

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
    public InforUserReadHisBo findUserReadHis(String userid, int type, String module,String className, Date
            halfTime) {
        Query query = new Query();
        Criteria criteria = new Criteria("userid").is(userid);
        criteria.and("module").is(module).and("type").is(type);
        if (StringUtils.isNotEmpty(className)) {
            criteria.and("className").is(className);
        }
        criteria.and("lastDate").gt(halfTime);
        query.addCriteria(criteria);
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

    @Override
    public InforUserReadHisBo findByReadHis(String userid, int type, String module, String className) {
        Query query = new Query();
        Criteria criteria = new Criteria("userid").is(userid).and("module").is(module).and("type").is(type);
        if (StringUtils.isNotEmpty(className)) {
            criteria.and("className").is(className);
        }
        query.addCriteria(criteria);
        return mongoTemplate.findOne(query, InforUserReadHisBo.class);
    }

    @Override
    public List<InforUserReadHisBo> findUserReadHisBeforeHalf(String userid, Date halfTime) {
        Query query = new Query();
        query.addCriteria(new Criteria("userid").is(userid));
        query.addCriteria(new Criteria("lastDate").lt(halfTime));
        return mongoTemplate.find(query, InforUserReadHisBo.class);
    }
}
