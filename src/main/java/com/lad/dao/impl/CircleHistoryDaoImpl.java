package com.lad.dao.impl;

import com.lad.bo.CircleHistoryBo;
import com.lad.dao.ICircleHistoryDao;
import com.lad.util.Constant;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/8/16
 */
@Repository("circleHistoryDao")
public class CircleHistoryDaoImpl implements ICircleHistoryDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public CircleHistoryBo insert(CircleHistoryBo circleHistoryBo) {
        mongoTemplate.insert(circleHistoryBo);
        return circleHistoryBo;
    }

    @Override
    public WriteResult updateHistory(String id, double[] position) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.set("position", position);
        update.set("updateTime", new Date());
        return mongoTemplate.updateFirst(query, update, CircleHistoryBo.class);
    }

    @Override
    public List<CircleHistoryBo> findNear(String cirlcid, String userid, double[] position, double maxDistance) {
        Point point = new Point(position[0],position[1]);

        Query query = new Query();
        Criteria criteria = Criteria.where("position").nearSphere(point).maxDistance(maxDistance/6378137.0);
        query.addCriteria(criteria);
        query.addCriteria(new Criteria("circleid").is(cirlcid));
        if (!StringUtils.isEmpty(userid)){
            query.addCriteria(new Criteria("userid").ne(userid));
        }
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "updateTime")));
        query.limit(20);
        return mongoTemplate.find(query, CircleHistoryBo.class);
    }

    @Override
    public List<CircleHistoryBo> findByCricleId(String circleid, Date time, int limit) {
        Query query = new Query();
        query.addCriteria(new Criteria("circleid").is(circleid));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "updateTime")));
        if (time != null) {
            query.addCriteria(new Criteria("updateTime").gt(time));
        }
        query.limit(limit);
        return mongoTemplate.find(query, CircleHistoryBo.class);
    }

    @Override
    public List<CircleHistoryBo> findByUserId(String userid, Date time, int limit) {
        Query query = new Query();
        query.addCriteria(new Criteria("userid").is(userid));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "updateTime")));
        if (time != null) {
            query.addCriteria(new Criteria("updateTime").gt(time));
        }
        query.limit(limit);
        return mongoTemplate.find(query, CircleHistoryBo.class);
    }

    @Override
    public CircleHistoryBo findByUserIdAndCircleId(String userid, String circleid) {
        Query query = new Query();
        query.addCriteria(new Criteria("userid").is(userid));
        query.addCriteria(new Criteria("circleid").is(circleid));
        return mongoTemplate.findOne(query, CircleHistoryBo.class);
    }
}
