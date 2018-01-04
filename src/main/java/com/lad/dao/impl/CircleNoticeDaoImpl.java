package com.lad.dao.impl;

import com.lad.bo.CircleNoticeBo;
import com.lad.dao.ICircleNoticeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

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
    public List<CircleNoticeBo> findCircleNotice(String circleid, int page, int limit) {
        Query query = new Query();
        query.addCriteria(new Criteria("circleid").is(circleid));
        query.with(new Sort(Sort.Direction.DESC, "_id"));
        query.skip((page -1) * limit);
        query.limit(limit);
        return mongoTemplate.find(query, CircleNoticeBo.class);
    }

    @Override
    public CircleNoticeBo findLastNotice(String circleid) {
        Query query = new Query();
        query.addCriteria(new Criteria("circleid").is(circleid));
        query.with(new Sort(Sort.Direction.DESC, "_id"));
        query.limit(1);
        return mongoTemplate.findOne(query, CircleNoticeBo.class);
    }
}
