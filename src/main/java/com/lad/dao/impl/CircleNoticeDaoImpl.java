package com.lad.dao.impl;

import com.lad.bo.CircleNoticeBo;
import com.lad.dao.ICircleNoticeDao;
import com.lad.util.Constant;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2018
 * Version: 1.0
 * Time:2018/1/4
 */
@Repository("circleNoticeDao")
public class CircleNoticeDaoImpl implements ICircleNoticeDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public CircleNoticeBo addNotice(CircleNoticeBo noticeBo) {
        mongoTemplate.insert(noticeBo);
        return noticeBo;
    }

    @Override
    public List<CircleNoticeBo> findCircleNotice(String targetid, int noticeType, int page, int limit) {
        Query query = new Query();
        Criteria criteria = new Criteria("deleted").is(Constant.ACTIVITY).and("noticeType").is(noticeType);
        if (noticeType == 0) {
            criteria.and("circleid").is(targetid);
        } else {
            criteria.and("chatroomid").is(targetid);
        }
        query.addCriteria(criteria);
        query.with(new Sort(Sort.Direction.DESC, "_id"));
        query.skip((page -1) * limit);
        query.limit(limit);
        return mongoTemplate.find(query, CircleNoticeBo.class);
    }

    @Override
    public CircleNoticeBo findLastNotice(String targetid, int noticeType) {
        Query query = new Query();
        Criteria criteria = new Criteria("deleted").is(Constant.ACTIVITY).and("noticeType").is(noticeType);
        if (noticeType == 0) {
            criteria.and("circleid").is(targetid);
        } else {
            criteria.and("chatroomid").is(targetid);
        }
        query.addCriteria(criteria);
        query.with(new Sort(Sort.Direction.DESC, "_id"));
        query.limit(1);
        return mongoTemplate.findOne(query, CircleNoticeBo.class);
    }

    @Override
    public CircleNoticeBo findNoticeById(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id).and("deleted").is(Constant.ACTIVITY));
        return mongoTemplate.findOne(query, CircleNoticeBo.class);
    }

    @Override
    public WriteResult deleteNotice(String id, String userid) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.set("deleted", Constant.DELETED);
        update.set("updateuid", userid);
        update.set("type", 2);
        return mongoTemplate.updateFirst(query, update, CircleNoticeBo.class);
    }

    @Override
    public WriteResult updateNoticeRead(String id, LinkedHashSet<String> readUsers, LinkedHashSet<String>
            unReadUsers) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.set("unReadUsers", unReadUsers);
        update.set("readUsers", readUsers);
        return mongoTemplate.updateFirst(query, update, CircleNoticeBo.class);
    }

    @Override
    public WriteResult updateNotice(CircleNoticeBo noticeBo) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(noticeBo.getId()));
        Update update = new Update();
        update.set("title", noticeBo.getTitle());
        update.set("content", noticeBo.getContent());
        update.set("images", noticeBo.getImages());
        update.set("updateuid", noticeBo.getUpdateuid());
        update.set("updateTime", noticeBo.getUpdateTime());
        update.set("type", noticeBo.getType());
        return mongoTemplate.updateFirst(query, update, CircleNoticeBo.class);
    }

    @Override
    public List<CircleNoticeBo> unReadNotice(String userid, String targetid, int noticeType) {
        Query query = new Query();
        Criteria criteria = new Criteria("deleted").is(Constant.ACTIVITY).and("noticeType").is(noticeType);
        if (noticeType == 0) {
            criteria.and("circleid").is(targetid);
        } else {
            criteria.and("chatroomid").is(targetid);
        }
        criteria.and("unReadUsers").in(userid);
        query.addCriteria(criteria);
        query.with(new Sort(Sort.Direction.DESC, "_id"));
        return mongoTemplate.find(query, CircleNoticeBo.class);
    }

    @Override
    public List<CircleNoticeBo> unReadNotice(String userid, String targetid, int noticeType, int page, int limit) {
        Query query = new Query();
        Criteria criteria = new Criteria("deleted").is(Constant.ACTIVITY).and("noticeType").is(noticeType);
        if (noticeType == 0) {
            criteria.and("circleid").is(targetid);
        } else {
            criteria.and("chatroomid").is(targetid);
        }
        criteria.and("unReadUsers").in(userid);
        query.addCriteria(criteria);
        query.with(new Sort(Sort.Direction.DESC, "_id"));
        query.skip((page -1) * limit);
        query.limit(limit);
        return mongoTemplate.find(query, CircleNoticeBo.class);
    }

    @Override
    public List<CircleNoticeBo> findNoticeByIds(String... ids) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").in(ids).and("deleted").is(Constant.ACTIVITY));
        return mongoTemplate.find(query, CircleNoticeBo.class);
    }

    @Override
    public long findNoticeTotal(String targetid, int noticeType) {
        Query query = new Query();
        Criteria criteria = new Criteria("deleted").is(Constant.ACTIVITY).and("noticeType").is(noticeType);
        if (noticeType == 0) {
            criteria.and("circleid").is(targetid);
        } else {
            criteria.and("chatroomid").is(targetid);
        }
        query.addCriteria(criteria);
        return mongoTemplate.count(query, CircleNoticeBo.class);
    }
}
