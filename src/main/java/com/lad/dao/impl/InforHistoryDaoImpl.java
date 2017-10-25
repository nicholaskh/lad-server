package com.lad.dao.impl;

import com.lad.bo.InforHistoryBo;
import com.lad.dao.IInforHistoryDao;
import com.lad.util.Constant;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/10/25
 */
@Repository("inforHistoryDao")
public class InforHistoryDaoImpl implements IInforHistoryDao{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public InforHistoryBo addInfoHis(InforHistoryBo historyBo) {
        mongoTemplate.insert(historyBo);
        return historyBo;
    }

    @Override
    public InforHistoryBo findTodayHis(String inforid, Date zeroTime) {
        Query query = new Query();
        query.addCriteria(new Criteria("inforid").is(inforid));
        query.addCriteria(new Criteria("readDate").is(zeroTime));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        return mongoTemplate.findOne(query,InforHistoryBo.class);
    }

    @Override
    public WriteResult findHalfYearHis(String inforid, Date halfYearTime) {
        Query query = new Query();
        query.addCriteria(new Criteria("inforid").is(inforid));
        query.addCriteria(new Criteria("readDate").lt(halfYearTime));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        Update update = new Update();
        update.set("deleted", Constant.DELETED);
        return mongoTemplate.updateMulti(query, update, InforHistoryBo.class);
    }

    @Override
    public WriteResult deleteHis(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.set("deleted", Constant.DELETED);
        return mongoTemplate.updateFirst(query, update, InforHistoryBo.class);
    }

    @Override
    public WriteResult updateHisDayNum(String id, int num) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.inc("dayNum", num);
        return mongoTemplate.updateFirst(query, update, InforHistoryBo.class);
    }
}
