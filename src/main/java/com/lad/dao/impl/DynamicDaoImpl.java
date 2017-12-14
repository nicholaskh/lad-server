package com.lad.dao.impl;

import com.lad.bo.DynamicBo;
import com.lad.dao.IDynamicDao;
import com.lad.util.Constant;
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
                update.set("transNum", num);
                break;
        }
        return mongoTemplate.updateFirst(query, update, DynamicBo.class);
    }

    @Override
    public DynamicBo findByMsgid(String msgid) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(msgid).and("deleted").is(Constant.ACTIVITY));
        return mongoTemplate.findOne(query, DynamicBo.class);
    }

    @Override
    public List<DynamicBo> findAllFriendsMsg(List<String> friendids, int page, int limit) {
        Query query = new Query();
        query.addCriteria(new Criteria("createuid").in(friendids).and("deleted").is(Constant.ACTIVITY));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
        page = page < 1 ? 1 :page;
        query.skip((page -1)*limit);
        query.limit(limit);
        return mongoTemplate.find(query, DynamicBo.class);
    }

    @Override
    public List<DynamicBo> findAFriendsMsg(String friendid, int page, int limit) {
        Query query = new Query();
        query.addCriteria(new Criteria("createuid").is(friendid).and("deleted").is(Constant.ACTIVITY));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
        page = page < 1 ? 1 :page;
        query.skip((page -1)*limit);
        query.limit(limit);
        return mongoTemplate.find(query, DynamicBo.class);
    }
}
