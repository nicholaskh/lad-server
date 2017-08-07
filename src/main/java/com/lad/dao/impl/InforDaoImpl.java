package com.lad.dao.impl;

import com.lad.dao.IInforDao;
import com.lad.scrapybo.InforBo;
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
 * Time:2017/8/1
 */
@Repository("inforDao")
public class InforDaoImpl implements IInforDao {
    
    @Autowired
    @Qualifier("mongoTemplateTwo")
    private MongoTemplate mongoTemplateTwo;

    @Override
    public List<InforBo> selectAllInfos() {
        ProjectionOperation project = Aggregation.project("_id","className", "classNum");
        GroupOperation groupOperation = Aggregation.group("className", "classNum").count().as("nums");
        Aggregation aggregation = Aggregation.newAggregation(project, groupOperation,
                Aggregation.sort(Sort.Direction.DESC, "_id"));
        AggregationResults<InforBo> results = mongoTemplateTwo.aggregate(aggregation, "test", InforBo.class);
        return results.getMappedResults();
    }




    public List<InforBo> selectByLike(){


        return null;
    }

    public List<InforBo> findGroups(String module){
        return null;
    }

    public List<InforBo> findByList(String className, String createTime, int limit){
        Query query = new Query();
        query.addCriteria(new Criteria("className").is(className));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "time")));
        if (!StringUtils.isEmpty(createTime)) {
            query.addCriteria(new Criteria("time").lt(createTime));
        }
        query.limit(limit);
        return mongoTemplateTwo.find(query, InforBo.class);
    }

    public InforBo findById(String id){
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        return mongoTemplateTwo.findOne(query, InforBo.class);
    }


    
}
