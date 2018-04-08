package com.lad.dao;

import com.lad.scrapybo.DailynewsBo;
import com.mongodb.WriteResult;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2018
 * Version: 1.0
 * Time:2018/4/5
 */
@Repository("dailynewsDao")
public class DailynewsDao extends InforBaseDao<DailynewsBo> {


    /**
     * 查找所有分类信息
     * @return
     */
    public List<DailynewsBo> selectGroups(){
        ProjectionOperation project = Aggregation.project("_id","className");
        GroupOperation groupOperation = Aggregation.group("className").count().as("nums");
        Aggregation aggregation = Aggregation.newAggregation(project, groupOperation,
                Aggregation.sort(Sort.Direction.DESC, "_id"));
        AggregationResults<DailynewsBo> results = getMongoTemplateTwo().aggregate(aggregation, "dailynews",
                DailynewsBo.class);
        return results != null ? results.getMappedResults() : null;
    }

}
