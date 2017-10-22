package com.lad.dao.impl;

import com.lad.bo.UserTagBo;
import com.lad.dao.IUserTagDao;
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
 * Time:2017/10/3
 */
@Repository("userTagDao")
public class UserTagDaoImpl implements IUserTagDao{

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public UserTagBo insert(UserTagBo tagBo) {
        mongoTemplate.insert(tagBo);
        return tagBo;
    }

    @Override
    public WriteResult delete(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        return mongoTemplate.remove(query, UserTagBo.class);
    }

    @Override
    public UserTagBo findByName(String name, String userid) {
        Query query = new Query();
        query.addCriteria(new Criteria("userid").is(userid));
        query.addCriteria(new Criteria("name").is(name));
        return mongoTemplate.findOne(query, UserTagBo.class);
    }

    @Override
    public List<UserTagBo> findByUserid(String userid, int type) {
        Query query = new Query();
        query.addCriteria(new Criteria("userid").is(userid));
        query.addCriteria(new Criteria("type").is(type));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC,"tagTimes")));
        return mongoTemplate.find(query, UserTagBo.class);
    }

    @Override
    public WriteResult updateTimes(String name, String userid, int type) {
        Query query = new Query();
        query.addCriteria(new Criteria("userid").is(userid));
        query.addCriteria(new Criteria("name").is(name));
        query.addCriteria(new Criteria("type").is(type));
        Update update = new Update();
        update.inc("tagTimes", 1);
        return mongoTemplate.updateFirst(query, update, UserTagBo.class);
    }
}
