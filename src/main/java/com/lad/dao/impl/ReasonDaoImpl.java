package com.lad.dao.impl;

import com.lad.bo.ReasonBo;
import com.lad.dao.IReasonDao;
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
 * Version: 1.0
 * Time:2017/7/5
 */
@Repository("reasonDao")
public class ReasonDaoImpl implements IReasonDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ReasonBo insert(ReasonBo reasonBo) {
        mongoTemplate.insert(reasonBo);
        return reasonBo;
    }

    @Override
    public WriteResult updateApply(String id, int status, String refuse) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        Update update = new Update();
        update.set("status", status);
        if (status == Constant.ADD_REFUSE) {
            update.set("refuse", refuse);
        }
        return mongoTemplate.updateFirst(query, update, ReasonBo.class);
    }


    @Override
    public ReasonBo findByUserAndCircle(String userid, String circleid) {
        Query query = new Query();
        query.addCriteria(new Criteria("userid").is(userid));
        query.addCriteria(new Criteria("circleid").is(circleid));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        query.addCriteria(new Criteria("status").is(Constant.ADD_APPLY));
        return mongoTemplate.findOne(query, ReasonBo.class);
    }

    @Override
    public ReasonBo findById(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        return mongoTemplate.findOne(query, ReasonBo.class);
    }


    @Override
    public WriteResult deleteById(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.set("deleted", Constant.DELETED);
        return mongoTemplate.updateFirst(query, update, ReasonBo.class);
    }

    @Override
    public List<ReasonBo> findByCircle(String circleid) {
        Query query = new Query();
        query.addCriteria(new Criteria("circleid").is(circleid));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        return mongoTemplate.find(query, ReasonBo.class);
    }
}
