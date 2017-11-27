package com.lad.dao.impl;

import com.lad.bo.ReasonBo;
import com.lad.dao.IReasonDao;
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
        query.addCriteria(new Criteria("createuid").is(userid));
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
        query.addCriteria(new Criteria("status").is(Constant.ADD_APPLY));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));
        return mongoTemplate.find(query, ReasonBo.class);
    }


    public List<ReasonBo> findByChatroom(String chatroomid) {
        Query query = new Query();
        query.addCriteria(new Criteria("chatroomid").is(chatroomid));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        query.addCriteria(new Criteria("status").is(Constant.ADD_APPLY));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));
        return mongoTemplate.find(query, ReasonBo.class);
    }


    public ReasonBo findByUserAndChatroom(String userid, String chatroomid) {
        Query query = new Query();
        query.addCriteria(new Criteria("chatroomid").is(chatroomid));
        query.addCriteria(new Criteria("createuid").is(userid));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        query.addCriteria(new Criteria("status").is(Constant.ADD_APPLY));
        return mongoTemplate.findOne(query, ReasonBo.class);
    }

    @Override
    public List<ReasonBo> findByCircleHis(String circleid, int page, int limit) {
        Query query = new Query();
        query.addCriteria(new Criteria("circleid").is(circleid));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));
        query.skip(page < 1 ? 1: page);
        query.limit(limit);
        return mongoTemplate.find(query, ReasonBo.class);
    }

    @Override
    public List<ReasonBo> findByChatroomHis(String chatroomid, int page, int limit) {
        Query query = new Query();
        query.addCriteria(new Criteria("chatroomid").is(chatroomid));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));
        query.skip(page < 1 ? 1: page);
        query.limit(limit);
        return mongoTemplate.find(query, ReasonBo.class);
    }
}
