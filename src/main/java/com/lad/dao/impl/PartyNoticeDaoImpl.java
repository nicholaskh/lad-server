package com.lad.dao.impl;

import com.lad.bo.PartyNoticeBo;
import com.lad.dao.IPartyNoticeDao;
import com.lad.util.Constant;
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
 * Version: 1.0
 * Time:2017/11/25
 */
@Repository("partyNoticeDao")
public class PartyNoticeDaoImpl implements IPartyNoticeDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public PartyNoticeBo addPartyNotice(PartyNoticeBo noticeBo) {
        mongoTemplate.insert(noticeBo);
        return noticeBo;
    }

    @Override
    public PartyNoticeBo findNoticeById(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        return mongoTemplate.findOne(query, PartyNoticeBo.class);
    }

    @Override
    public WriteResult deleteNotice(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.set("deleted", Constant.DELETED);
        return mongoTemplate.updateFirst(query, update, PartyNoticeBo.class);
    }

    @Override
    public List<PartyNoticeBo> findByPartyid(String partyid, int page, int limit) {
        Query query = new Query();
        query.addCriteria(new Criteria("partyid").is(partyid));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        query.skip(page < 1 ? 1 : page);
        query.limit(limit);
        return mongoTemplate.find(query, PartyNoticeBo.class);
    }

    @Override
    public List<PartyNoticeBo> findByUserid(String userid, int page, int limit) {
        Query query = new Query();
        query.addCriteria(new Criteria("users").in(userid));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        query.skip(page < 1 ? 1 : page);
        query.limit(limit);
        return mongoTemplate.find(query, PartyNoticeBo.class);
    }

    @Override
    public PartyNoticeBo findPartyNotice(String partyid) {
        Query query = new Query();
        query.addCriteria(new Criteria("partyid").is(partyid));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        return mongoTemplate.findOne(query, PartyNoticeBo.class);
    }
}
