package com.lad.dao.impl;

import com.lad.bo.UserVisitBo;
import com.lad.dao.IUserVisitDao;
import com.lad.util.Constant;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
 * Time:2017/12/18
 */
@Repository("userVisitDao")
public class UserVisitDaoImpl implements IUserVisitDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public UserVisitBo addUserVisit(UserVisitBo userVisitBo) {
        mongoTemplate.insert(userVisitBo);
        return userVisitBo;
    }

    @Override
    public WriteResult updateUserVisit(String id, Date date) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.set("visitTime", date);
        return mongoTemplate.updateFirst(query, update, UserVisitBo.class);
    }

    @Override
    public List<UserVisitBo> visitFromMeList(String userid, int type, int page, int limit) {
        Query query = new Query();
        query.addCriteria(new Criteria("visitid").is(userid).and("type").is(type)
                .and("deleted").is(Constant.ACTIVITY));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "visitTime")));
        page = page < 1 ? 1 : page;
        query.skip( (page - 1) * limit);
        query.limit(limit);
        return mongoTemplate.find(query, UserVisitBo.class);
    }

    @Override
    public List<UserVisitBo> visitToMeList(String userid, int type,int page, int limit) {
        Query query = new Query();
        query.addCriteria(new Criteria("ownerid").is(userid).and("type").is(type)
                .and("deleted").is(Constant.ACTIVITY));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "visitTime")));
        page = page < 1 ? 1 : page;
        query.skip( (page - 1) * limit);
        query.limit(limit);
        return mongoTemplate.find(query, UserVisitBo.class);
    }

    @Override
    public WriteResult deleteUserVisit(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.set("deleted", Constant.DELETED);
        return mongoTemplate.updateFirst(query, update, UserVisitBo.class);
    }

    @Override
    public UserVisitBo findUserVisit(String ownerid, String visitid, int type) {
        Query query = new Query();
        query.addCriteria(new Criteria("ownerid").is(ownerid).and("visitid")
                .is(visitid).and("type").is(type).and("deleted").is(Constant.ACTIVITY));
        return mongoTemplate.findOne(query, UserVisitBo.class);
    }
}
