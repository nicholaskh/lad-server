package com.lad.dao.impl;

import com.lad.bo.UserReadHisBo;
import com.lad.dao.IUserReadHisDao;
import com.mongodb.WriteResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2018
 * Version: 1.0
 * Time:2018/2/7
 */
@Repository("userReadHisDao")
public class UserReadHisDaoImpl implements IUserReadHisDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public UserReadHisBo addUserReadHis(UserReadHisBo hisBo) {
        mongoTemplate.insert(hisBo);
        return hisBo;
    }

    @Override
    public UserReadHisBo findByType(String userid, int inforType, String module, String className) {
        Query query = new Query();
        Criteria criteria = new Criteria("inforType").is(inforType);
        criteria.and("userid").is(userid);
        if (StringUtils.isNotEmpty(module)) {
            criteria.and("module").is(module);
        }
        if (StringUtils.isNotEmpty(className)) {
            criteria.and("className").is(className);
        }
        query.with(new Sort(Sort.Direction.DESC, "lastTime"));
        query.limit(1);
        return mongoTemplate.findOne(query, UserReadHisBo.class);
    }

    @Override
    public WriteResult updateUserReadHis(String id, String inforid) {
        Query query = new Query();
        Criteria criteria = new Criteria("_id").is(id);
        Update update = new Update();
        update.set("inforid", inforid);
        update.set("lastTime", new Date());
        return mongoTemplate.updateFirst(query, update, UserReadHisBo.class);
    }

    @Override
    public UserReadHisBo findMyLastRead(String userid) {
        Query query = new Query(new Criteria("userid").is(userid));
        query.with(new Sort(Sort.Direction.DESC, "lastTime"));
        query.limit(1);
        return mongoTemplate.findOne(query, UserReadHisBo.class);
    }

    @Override
    public UserReadHisBo findByInforid(String userid, String inforid) {
        Query query = new Query(new Criteria("userid").is(userid).and("inforid").is(inforid));
        return mongoTemplate.findOne(query, UserReadHisBo.class);
    }

    @Override
    public WriteResult updateUserReadHis(String id) {
        Query query = new Query();
        Criteria criteria = new Criteria("_id").is(id);
        Update update = new Update();
        update.set("lastTime", new Date());
        return mongoTemplate.updateFirst(query, update, UserReadHisBo.class);
    }

    @Override
    public WriteResult deleteUserReadHis(String userid, int inforType) {
        Query query = new Query(new Criteria("userid").is(userid).and("inforType").is(inforType));
        query.with(new Sort(Sort.Direction.DESC, "lastTime"));
        query.skip(5);
        return mongoTemplate.remove(query, UserReadHisBo.class);
    }

    @Override
    public List<UserReadHisBo> findByUserAndInfor(String userid, int inforType) {
        Query query = new Query();
        Criteria criteria = new Criteria("userid").is(userid);
        if (inforType != 0) {
            criteria.and("inforType").is(inforType);
        }
        query.addCriteria(criteria);
        query.with(new Sort(Sort.Direction.DESC, "lastTime"));
        if (inforType != 0) {
            query.limit(5);
        } 
        return mongoTemplate.find(query, UserReadHisBo.class);
    }

    @Override
    public WriteResult deleteUserReadHis(List<String> removeIds) {
        Query query = new Query();
        Criteria criteria = new Criteria("_id").in(removeIds);
        return mongoTemplate.remove(query, UserReadHisBo.class);
    }
}
