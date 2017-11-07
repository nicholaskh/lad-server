package com.lad.dao.impl;

import com.lad.bo.PartyUserBo;
import com.lad.dao.IPartyUserDao;
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
 * Time:2017/9/30
 */
@Repository("partyUserDao")
public class PartyUserDaoImpl implements IPartyUserDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public PartyUserBo insert(PartyUserBo partyUserBo) {
        mongoTemplate.insert(partyUserBo);
        return partyUserBo;
    }

    @Override
    public WriteResult refuse(String id, String info) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        Update update = new Update();
        update.set("refuseInfo", info);
        return mongoTemplate.updateFirst(query, update, PartyUserBo.class);
    }

    @Override
    public WriteResult apply(String id, String info) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        Update update = new Update();
        update.set("joinInfo", info);
        return mongoTemplate.updateFirst(query, update, PartyUserBo.class);
    }

    @Override
    public List<PartyUserBo> findByParty(String partyid, int status) {
        Query query = new Query();
        query.addCriteria(new Criteria("partyid").is(partyid));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        if (status != -1) {
            query.addCriteria(new Criteria("status").is(status));
        }
        return mongoTemplate.find(query, PartyUserBo.class);
    }

    @Override
    public List<PartyUserBo>findByUserid(String userid, int status) {
        Query query = new Query();
        query.addCriteria(new Criteria("userid").is(userid));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        if (status != -1) {
            query.addCriteria(new Criteria("status").is(status));
        }
        return mongoTemplate.find(query, PartyUserBo.class);
    }

    @Override
    public WriteResult outParty(String partyid, String userid) {
        Query query = new Query();
        query.addCriteria(new Criteria("partyid").is(partyid));
        query.addCriteria(new Criteria("userid").is(userid));
        Update update = new Update();
        update.set("deleted", Constant.DELETED);
        return mongoTemplate.updateFirst(query, update, PartyUserBo.class);
    }

    @Override
    public List<PartyUserBo> findByPartyUsers(String partyid) {
        Query query = new Query();
        query.addCriteria(new Criteria("partyid").is(partyid));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        return mongoTemplate.find(query, PartyUserBo.class);
    }

    @Override
    public List<PartyUserBo> findPartyByUserid(String userid, int page, int limit) {
        Query query = new Query();
        query.addCriteria(new Criteria("userid").is(userid));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
        if (page < 1) {
            page = 1;
        }
        query.skip((page -1)*limit);
        query.limit(limit);
        return mongoTemplate.find(query, PartyUserBo.class);
    }

    @Override
    public WriteResult collectParty(String partyid, String userid, boolean isCollect) {
        Query query = new Query();
        query.addCriteria(new Criteria("partyid").is(partyid));
        query.addCriteria(new Criteria("userid").is(userid));
        Update update = new Update();
        if (isCollect) {
            update.set("collectParty", 1);
        } else {
            update.set("collectParty", 0);
        }
        return mongoTemplate.updateFirst(query, update, PartyUserBo.class);
    }

    @Override
    public PartyUserBo findPartyUser(String partyid, String userid) {
        Query query = new Query();
        query.addCriteria(new Criteria("partyid").is(partyid));
        query.addCriteria(new Criteria("userid").is(userid));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        return  mongoTemplate.findOne(query, PartyUserBo.class);
    }

    @Override
    public PartyUserBo findPartyUserIgnoreDel(String partyid, String userid) {
        Query query = new Query();
        query.addCriteria(new Criteria("partyid").is(partyid));
        query.addCriteria(new Criteria("userid").is(userid));
        return  mongoTemplate.findOne(query, PartyUserBo.class);
    }

    @Override
    public WriteResult deleteMulit(String partyid) {
        Query query = new Query();
        query.addCriteria(new Criteria("partyid").is(partyid));
        Update update = new Update();
        update.set("deleted", Constant.DELETED);
        return mongoTemplate.updateMulti(query, update, PartyUserBo.class);
    }
}
