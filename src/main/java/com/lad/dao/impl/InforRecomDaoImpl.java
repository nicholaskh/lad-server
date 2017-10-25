package com.lad.dao.impl;

import com.lad.bo.InforRecomBo;
import com.lad.dao.IInforRecomDao;
import com.lad.util.Constant;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/10/25
 */
@Repository("inforRecomDao")
public class InforRecomDaoImpl implements IInforRecomDao{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public InforRecomBo addInforRecom(InforRecomBo recomBo) {
        mongoTemplate.insert(recomBo);
        return recomBo;
    }

    @Override
    public InforRecomBo findRecomByInforid(String inforid) {
        Query query = new Query();
        query.addCriteria(new Criteria("inforid").is(inforid));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        return mongoTemplate.findOne(query, InforRecomBo.class);
    }

    @Override
    public WriteResult updateRecomByInforid(String id, int halfNum, int totalNum) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.inc("halfyearNum", halfNum);
        update.inc("totalNum", totalNum);
        return mongoTemplate.updateFirst(query, update, InforRecomBo.class);
    }

    @Override
    public List<InforRecomBo> findRecomByTypeAndModule(int type, LinkedHashSet<String> modules) {
        Query query = new Query();
        query.addCriteria(new Criteria("type").is(type));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        query.addCriteria(new Criteria("module").in(modules));
        return mongoTemplate.find(query, InforRecomBo.class);
    }

    @Override
    public List<InforRecomBo> findRecomByType(int type) {
        Query query = new Query();
        query.addCriteria(new Criteria("type").is(type));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        return mongoTemplate.find(query, InforRecomBo.class);
    }
}
