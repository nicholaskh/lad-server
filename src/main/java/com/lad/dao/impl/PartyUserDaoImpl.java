package com.lad.dao.impl;

import com.lad.bo.PartyUserBo;
import com.lad.dao.IPartyUserDao;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
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
        Update update = new Update();
        update.set("refuseInfo", info);
        return mongoTemplate.updateFirst(query, update, PartyUserBo.class);
    }

    @Override
    public WriteResult apply(String id, String info) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.set("joinInfo", info);
        return mongoTemplate.updateFirst(query, update, PartyUserBo.class);
    }

    @Override
    public List<PartyUserBo> findByParty(String partyid, int status) {
        Query query = new Query();
        query.addCriteria(new Criteria("partyid").is(partyid));
        if (status != -1) {
            query.addCriteria(new Criteria("status").is(status));
        }
        return mongoTemplate.find(query, PartyUserBo.class);
    }

    @Override
    public List<PartyUserBo>findByUserid(String userid, int status) {
        Query query = new Query();
        query.addCriteria(new Criteria("userid").is(userid));
        if (status != -1) {
            query.addCriteria(new Criteria("status").is(status));
        }
        return mongoTemplate.find(query, PartyUserBo.class);
    }
}
