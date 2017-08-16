package com.lad.dao.impl;

import com.lad.bo.RedstarBo;
import com.lad.dao.IRedstarDao;
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
 * Time:2017/6/29
 */
@Repository
public class RedstarDaoImpl implements IRedstarDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    public RedstarBo insert(RedstarBo redstarBo){
        mongoTemplate.insert(redstarBo);
        return redstarBo;
    }

    public WriteResult addCommentCount(String userid, String circleid, int count){
        Query query = new Query();
        query.addCriteria(new Criteria("userid").is(userid));
        query.addCriteria(new Criteria("circleid").is(circleid));
        query.addCriteria(new Criteria("deleted").is(0));
        Update update = new Update();
        update.inc("commentTotal", count);
        update.inc("commentWeek", count);
        return mongoTemplate.updateFirst(query, update, RedstarBo.class);
    }

    public  WriteResult updateRedWeekByUser(String userid, int weekNo, int year){
        Query query = new Query();
        query.addCriteria(new Criteria("userid").is(userid));
        query.addCriteria(new Criteria("deleted").is(0));
        Update update = new Update();
        update.set("commentWeek", 1);
        update.inc("commentTotal", 1);
        update.set("weekNo", weekNo);
        update.set("year", year);
        return mongoTemplate.updateMulti(query, update, RedstarBo.class);
    }

    public  WriteResult updateRedWeek(int weekNo){
        Query query = new Query();
        query.addCriteria(new Criteria("deleted").is(0));
        query.addCriteria(new Criteria("weekNo").ne(weekNo));
        query.addCriteria(new Criteria("commentWeek").gt(0));
        Update update = new Update();
        update.set("commentWeek", 1);
        update.set("weekNo", weekNo);
        return mongoTemplate.updateMulti(query, update, RedstarBo.class);
    }

    public List<RedstarBo> findRedTotal(String circleid){
        Query query = new Query();
        query.addCriteria(new Criteria("deleted").is(0));
        query.addCriteria(new Criteria("circleid").is(circleid));
        query.with(new Sort(Sort.Direction.DESC, "commentTotal"));
        query.limit(10);
        return mongoTemplate.find(query, RedstarBo.class);
    }

    public List<RedstarBo> findRedWeek(String circleid, int weekNo, int year){
        Query query = new Query();
        query.addCriteria(new Criteria("deleted").is(0));
        query.addCriteria(new Criteria("circleid").is(circleid));
        query.addCriteria(new Criteria("weekNo").is(weekNo));
        query.addCriteria(new Criteria("year").is(year));
        query.with(new Sort(Sort.Direction.DESC, "commentWeek"));
        query.limit(10);
        return mongoTemplate.find(query, RedstarBo.class);
    }

    public RedstarBo findByUserAndCircle(String userid, String circleid){
        Query query = new Query();
        query.addCriteria(new Criteria("deleted").is(0));
        query.addCriteria(new Criteria("circleid").is(circleid));
        query.addCriteria(new Criteria("userid").is(userid));
        return mongoTemplate.findOne(query, RedstarBo.class);
    }


}
