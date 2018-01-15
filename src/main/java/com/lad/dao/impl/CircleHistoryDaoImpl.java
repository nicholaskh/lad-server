package com.lad.dao.impl;

import com.lad.bo.CircleHistoryBo;
import com.lad.dao.ICircleHistoryDao;
import com.lad.util.Constant;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
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
        Distance distance = new Distance(maxDistance/1000, Metrics.KILOMETERS);
        Circle circle = new Circle(point, distance);
        Criteria criteria = Criteria.where("position").withinSphere(circle);
        query.addCriteria(criteria);
        query.addCriteria(new Criteria("circleid").is(cirlcid).and("type").is(0).and("deleted").is(0));
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
        query.addCriteria(new Criteria("circleid").is(circleid).and("type").is(0).and("deleted").is(0));
        return mongoTemplate.findOne(query, CircleHistoryBo.class);
    }

    @Override
    public CircleHistoryBo findCircleHisById(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id).and("deleted").is(0));
        return mongoTemplate.findOne(query, CircleHistoryBo.class);
    }

    @Override
    public List<CircleHistoryBo> findCircleHisByUserid(String userid, int type, int page, int limit) {
        Query query = new Query();
        query.addCriteria(new Criteria("userid").is(userid).and("type").is(type).and("deleted").is(0));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
        page = page < 1 ? 1 : page;
        query.skip((page -1 )*limit);
        query.limit(limit);
        return mongoTemplate.find(query, CircleHistoryBo.class);
    }

    @Override
    public List<CircleHistoryBo> findCircleHisByCricleid(String circleid, int type, int page, int limit) {
        Query query = new Query();
        query.addCriteria(new Criteria("circleid").is(circleid).and("type").is(type).and("deleted").is(0));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
        page = page < 1 ? 1 : page;
        query.skip((page -1 )*limit);
        query.limit(limit);
        return mongoTemplate.find(query, CircleHistoryBo.class);
    }


    @Override
    public WriteResult deleteHis(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.set("deleted", Constant.DELETED);
        return mongoTemplate.updateFirst(query, update, CircleHistoryBo.class);
    }

    @Override
    public WriteResult deleteHisBitch(List<String> ids) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").in(ids));
        Update update = new Update();
        update.set("deleted", Constant.DELETED);
        return mongoTemplate.updateMulti(query, update, CircleHistoryBo.class);
    }
}
