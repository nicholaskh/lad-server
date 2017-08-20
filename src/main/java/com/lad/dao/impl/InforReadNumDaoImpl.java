package com.lad.dao.impl;

import com.lad.bo.InforReadNumBo;
import com.lad.dao.IInforReadNumDao;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;


/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/8/5
 */
@Repository("inforReadNumDao")
public class InforReadNumDaoImpl implements IInforReadNumDao {


    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public InforReadNumBo insert(InforReadNumBo readNumBo) {
        mongoTemplate.insert(readNumBo);
        return readNumBo;
    }

    @Override
    public WriteResult update(String inforid) {
        Query query = new Query();
        query.addCriteria(new Criteria("inforid").is(inforid));
        Update update = new Update();
        update.inc("visitNum", 1);
        return mongoTemplate.updateFirst(query, update, InforReadNumBo.class);
    }

    public InforReadNumBo findByInforid(String inforid) {
        Query query = new Query();
        query.addCriteria(new Criteria("inforid").is(inforid));
        return mongoTemplate.findOne(query, InforReadNumBo.class);
    }

    public WriteResult updateComment(String inforid, int number) {
        Query query = new Query();
        query.addCriteria(new Criteria("inforid").is(inforid));
        Update update = new Update();
        update.inc("commentNum", number);
        return mongoTemplate.updateFirst(query, update, InforReadNumBo.class);
    }

    public WriteResult updateThumpsub(String inforid, int number) {
        Query query = new Query();
        query.addCriteria(new Criteria("inforid").is(inforid));
        Update update = new Update();
        update.inc("thumpsubNum", number);
        return mongoTemplate.updateFirst(query, update, InforReadNumBo.class);
    }
}
