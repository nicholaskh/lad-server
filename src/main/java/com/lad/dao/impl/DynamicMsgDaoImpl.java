package com.lad.dao.impl;

import com.lad.bo.DynamicMsgBo;
import com.lad.dao.IDynamicMsgDao;
import com.lad.util.Constant;
import com.mongodb.WriteResult;
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
 * Time:2017/9/23
 */
@Repository("dynamicMsgDao")
public class DynamicMsgDaoImpl implements IDynamicMsgDao {

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public DynamicMsgBo insert(DynamicMsgBo dynamicMsgBo) {
        mongoTemplate.insert(dynamicMsgBo);
        return dynamicMsgBo;
    }

    @Override
    public WriteResult delete(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        return mongoTemplate.remove(query, DynamicMsgBo.class);
    }

    @Override
    public List<DynamicMsgBo> findAllFriendsMsg(List<String> friendids, int page, int limit) {

        Query query = new Query();
        query.addCriteria(new Criteria("userid").in(friendids));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));
        if (page < 1) {
            page = 1;
        }
        query.skip((page -1)*limit);
        query.limit(limit);
        return mongoTemplate.find(query, DynamicMsgBo.class);
    }

    @Override
    public List<DynamicMsgBo> findAFriendsMsg(String friendid, int page, int limit) {
        Query query = new Query();
        query.addCriteria(new Criteria("userid").is(friendid));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));
        if (page < 1) {
            page = 1;
        }
        query.skip((page -1)*limit);
        query.limit(limit);
        return mongoTemplate.find(query, DynamicMsgBo.class);
    }

    @Override
    public DynamicMsgBo findByTargetid(String tragetid, int type) {
        Query query = new Query();
        query.addCriteria(new Criteria("targetid").is(tragetid));
        query.addCriteria(new Criteria("dynamicType").is(type));
        return mongoTemplate.findOne(query, DynamicMsgBo.class);
    }
}
