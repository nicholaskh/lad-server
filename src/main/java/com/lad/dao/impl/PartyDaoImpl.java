package com.lad.dao.impl;

import com.lad.bo.PartyBo;
import com.lad.dao.IPartyDao;
import com.lad.util.Constant;
import com.mongodb.WriteResult;
import org.apache.commons.lang3.StringUtils;
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
 * Time:2017/9/7
 */

@Repository("partyDao")
public class PartyDaoImpl implements IPartyDao {

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public PartyBo insert(PartyBo partyBo) {
        mongoTemplate.insert(partyBo);
        return partyBo;
    }

    @Override
    public WriteResult update(PartyBo partyBo) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(partyBo.getId()));
        Update update = new Update();
        update.set("title", partyBo.getTitle());
        update.set("content", partyBo.getContent());
        update.set("startTime", partyBo.getStartTime());
        update.set("addrType", partyBo.getAddrType());
        update.set("addrInfo", partyBo.getAddrInfo());
        update.set("position", partyBo.getPosition());
        update.set("landmark", partyBo.getLandmark());
        update.set("payOrFree", partyBo.getPayOrFree());
        update.set("payAmount", partyBo.getPayAmount());
        update.set("payName", partyBo.getPayName());
        update.set("payInfo", partyBo.getPayInfo());
        update.set("appointment", partyBo.getAppointment());
        update.set("userLimit", partyBo.getUserLimit());
        update.set("isPhone", partyBo.isPhone());
        update.set("isOpen", partyBo.isOpen());
        update.set("reminder", partyBo.getReminder());
        return mongoTemplate.updateFirst(query, update, PartyBo.class);
    }

    @Override
    public WriteResult delete(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.set("deleted", Constant.DELETED);
        return mongoTemplate.updateFirst(query, update, PartyBo.class);
    }

    @Override
    public List<PartyBo> findByCreate(String createid, String start_id, int limit) {
        Query query = new Query();
        query.addCriteria(new Criteria("createuid").is(createid));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
        if (StringUtils.isNotEmpty(start_id)) {
            query.addCriteria(new Criteria("_id").lt(start_id));
        }
        query.limit(limit);
        return mongoTemplate.find(query, PartyBo.class);
    }

    @Override
    public List<PartyBo> findByMyJoin(String userid, String start_id, int limit) {
        return null;
    }
}
