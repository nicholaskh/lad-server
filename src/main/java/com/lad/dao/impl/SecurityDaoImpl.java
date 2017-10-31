package com.lad.dao.impl;

import com.lad.dao.ISecurityDao;
import com.lad.scrapybo.SecurityBo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/8/26
 */
@Repository("securityDao")
public class SecurityDaoImpl implements ISecurityDao {

    @Autowired
    @Qualifier("mongoTemplateTwo")
    private MongoTemplate mongoTemplateTwo;

    @Override
    public List<SecurityBo> findAllTypes() {
        ProjectionOperation project = Aggregation.project("_id","newsType");
        GroupOperation groupOperation = Aggregation.group("newsType").count().as("nums");
        Aggregation aggregation = Aggregation.newAggregation(project, groupOperation,
                Aggregation.sort(Sort.Direction.DESC, "_id"));
        AggregationResults<SecurityBo> results = mongoTemplateTwo.aggregate(aggregation, "security", SecurityBo
                .class);
        return results.getMappedResults();
    }

    @Override
    public List<SecurityBo> findByCity(String cityName, String createTime, int limit) {
        Query query = new Query();
        query.addCriteria(new Criteria("city").is(cityName));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "time")));
        if (!StringUtils.isEmpty(createTime)) {
            query.addCriteria(new Criteria("time").lt(createTime));
        }
        query.limit(limit);
        return mongoTemplateTwo.find(query, SecurityBo.class);
    }

    @Override
    public List<SecurityBo> findByType(String typeName, String createTime, int limit) {
        Query query = new Query();
        query.addCriteria(new Criteria("newsType").is(typeName));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "time")));
        if (!StringUtils.isEmpty(createTime)) {
            query.addCriteria(new Criteria("time").lt(createTime));
        }
        query.limit(limit);
        return mongoTemplateTwo.find(query, SecurityBo.class);
    }

    @Override
    public SecurityBo findById(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        return mongoTemplateTwo.findOne(query, SecurityBo.class);
    }

    @Override
    public List<SecurityBo> findByLimiy(int limit) {
        Query query = new Query();
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "time")));
        query.limit(limit);
        return mongoTemplateTwo.find(query, SecurityBo.class);
    }
}
