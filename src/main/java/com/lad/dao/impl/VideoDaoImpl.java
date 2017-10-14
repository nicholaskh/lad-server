package com.lad.dao.impl;

import com.lad.dao.IVideoDao;
import com.lad.scrapybo.VideoBo;
import com.mongodb.WriteResult;
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
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/27
 */
@Repository("videoDao")
public class VideoDaoImpl implements IVideoDao {

    @Autowired
    @Qualifier("mongoTemplateTwo")
    private MongoTemplate mongoTemplateTwo;

    @Override
    public VideoBo findById(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        return mongoTemplateTwo.findOne(query, VideoBo.class);
    }

    @Override
    public List<VideoBo> findByPage(String groupName, int page, int limit) {
        Query query = new Query();
        query.addCriteria(new Criteria("module").is(groupName));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
        if (page < 1) {
            page = 1;
        }
        query.skip((page -1) * limit);
        query.limit(limit);
        return mongoTemplateTwo.find(query, VideoBo.class);
    }

    @Override
    public List<VideoBo> selectGroups() {
        ProjectionOperation project = Aggregation.project("_id","module");
        GroupOperation groupOperation = Aggregation.group("module").count().as("nums");
        Aggregation aggregation = Aggregation.newAggregation(project, groupOperation,
                Aggregation.sort(Sort.Direction.DESC, "_id"));
        AggregationResults<VideoBo> results = mongoTemplateTwo.aggregate(aggregation, "video", VideoBo.class);
        return results.getMappedResults();
    }

    @Override
    public List<VideoBo> selectClassByGroups(String groupName) {
        return null;
    }

    @Override
    public WriteResult updatePicById(String id, String pic) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.set("poster", pic);
        return mongoTemplateTwo.updateFirst(query, update, VideoBo.class);
    }
}
