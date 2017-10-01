package com.lad.dao.impl;

import com.lad.dao.IBroadcastDao;
import com.lad.scrapybo.BroadcastBo;
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

import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/29
 */
@Repository("broadcastDao")
public class BroadcastDaoImpl implements IBroadcastDao {

    @Autowired
    @Qualifier("mongoTemplateTwo")
    private MongoTemplate mongoTemplateTwo;

    @Override
    public BroadcastBo findById(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        return mongoTemplateTwo.findOne(query, BroadcastBo.class);
    }

    @Override
    public List<BroadcastBo> selectGroups() {
        ProjectionOperation project = Aggregation.project("_id","module");
        GroupOperation groupOperation = Aggregation.group("module").count().as("nums");
        Aggregation aggregation = Aggregation.newAggregation(project, groupOperation,
                Aggregation.sort(Sort.Direction.DESC, "_id"));
        AggregationResults<BroadcastBo> results = mongoTemplateTwo.aggregate(aggregation, "broadcast", BroadcastBo.class);
        return results.getMappedResults();
    }

    @Override
    public List<BroadcastBo> findByPage(String groupName, int page, int limit) {
        Query query = new Query();
        query.addCriteria(new Criteria("module").is(groupName));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
        if (page < 1) {
            page = 1;
        }
        query.skip((page -1) * limit);
        query.limit(limit);
        return mongoTemplateTwo.find(query, BroadcastBo.class);
    }

    @Override
    public List<BroadcastBo> selectClassByGroups(String groupName) {
        return null;
    }
}
