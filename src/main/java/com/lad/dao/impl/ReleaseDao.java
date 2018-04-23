package com.lad.dao.impl;

import com.lad.bo.ReleaseBo;
import com.lad.dao.IReleaseDao;
import com.lad.util.Constant;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2018
 * Time:2018/4/13
 */
@Repository("releaseDao")
public class ReleaseDao implements IReleaseDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ReleaseBo insert(ReleaseBo releaseBo) {
        mongoTemplate.insert(releaseBo);
        return releaseBo;
    }

    @Override
    public ReleaseBo findById(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        return mongoTemplate.findOne(query, ReleaseBo.class);
    }

    @Override
    public List<ReleaseBo> findByType(int releaseType, int page, int limit) {
        Query query = new Query();
        query.addCriteria(new Criteria("releaseType").is(releaseType).and("deleted").is(Constant.ACTIVITY));
        query.with(new Sort(Sort.Direction.DESC, "_id"));
        page = page < 1 ? 1 : page;
        query.skip((page - 1) *limit);
        query.limit(limit);
        return mongoTemplate.find(query, ReleaseBo.class);
    }

    @Override
    public List<ReleaseBo> findByParamsType(Map<String, Object> params, int releaseType, int page, int limit) {
        Query query = new Query();
        Criteria criteria = new Criteria("releaseType").is(releaseType).and("deleted").is(Constant.ACTIVITY);
        List<Criteria> criterias = new ArrayList<>();
        if (params != null && !params.isEmpty()) {
            params.forEach( (key, value) -> {
                Criteria or = new Criteria(key).is(value);
                criterias.add(or);
            });
            criteria.orOperator(criterias.toArray(new Criteria[]{}));
        }
        query.addCriteria(criteria);
        query.with(new Sort(Sort.Direction.DESC, "updateTime"));
        page = page < 1 ? 1 : page;
        query.skip((page - 1) *limit);
        query.limit(limit);
        return mongoTemplate.find(query, ReleaseBo.class);
    }

    @Override
    public WriteResult updateByParams(String id, Map<String, Object> params) {
        if (params != null && !params.isEmpty()) {
            Query query = new Query();
            query.addCriteria(new Criteria("_id").is(id));
            Update update = new Update();
            params.forEach( (key, value) -> update.set(key, value));
            return mongoTemplate.updateFirst(query, update, ReleaseBo.class);
        }
        return null;
    }

    @Override
    public WriteResult delete(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.set("deleted", Constant.DELETED);
        return mongoTemplate.updateFirst(query, update, ReleaseBo.class);
    }

    @Override
    public List<ReleaseBo> findByAgeWave(int startAge, int endAge, int releaseType, int page, int limit) {
        return null;
    }

    @Override
    public List<ReleaseBo> findByWageWave(int start, int end, int releaseType, int page, int limit) {
        return null;
    }

    @Override
    public List<ReleaseBo> findTourAddrs(String addrs, int releaseType, int page, int limit) {
        return null;
    }

    @Override
    public List<ReleaseBo> findByShowTyps(String types, int releaseType, int page, int limit) {
        return null;
    }
}
