package com.lad.dao.impl;

import com.lad.dao.IVideoDao;
import com.lad.scrapybo.VideoBo;
import com.lad.util.CommonUtil;
import com.lad.util.Constant;
import com.mongodb.WriteResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
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

        Criteria criteria = new Criteria("module").is(groupName);
        MatchOperation match = Aggregation.match(criteria);
        GroupOperation groupOperation = Aggregation.group("className")
                .sum("visitNum").as("visitNum");
        Aggregation aggregation = Aggregation.newAggregation(match, groupOperation,
                Aggregation.sort(Sort.Direction.ASC, "className"));
        AggregationResults<VideoBo> results = mongoTemplateTwo.aggregate(aggregation, "video", VideoBo.class);
        return results.getMappedResults();
    }

    @Override
    public List<VideoBo> findByClassNamePage(String module, String className, int page, int limit) {
        Query query = new Query();
        query.addCriteria(new Criteria("module").is(module));
        if (StringUtils.isNotEmpty(className)) {
            query.addCriteria(new Criteria("className").is(className));
        }
        query.with(new Sort(new Sort.Order(Sort.Direction.ASC,"num")));
        page = page < 1 ? 1 : page;
        query.skip(page - 1);
        query.limit(limit);
        return mongoTemplateTwo.find(query, VideoBo.class);
    }

    @Override
    public WriteResult updatePicById(String id, String pic) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.set("poster", pic);
        return mongoTemplateTwo.updateFirst(query, update, VideoBo.class);
    }

    @Override
    public List<VideoBo> findByLimit(int limit) {
        Query query = new Query();
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
        query.limit(limit);
        return mongoTemplateTwo.find(query, VideoBo.class);
    }

    @Override
    public WriteResult updateVideoNum(String inforid, int type, int num) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(inforid));
        Update update = new Update();
        switch (type){
            case Constant.VISIT_NUM:
                update.inc("visitNum", num);
                break;
            case Constant.COMMENT_NUM:
                update.inc("commnetNum", num);
                break;
            case Constant.SHARE_NUM:
                update.inc("shareNum", num);
                break;
            case Constant.THUMPSUB_NUM:
                update.inc("thumpsubNum", num);
                break;
            case Constant.COLLECT_NUM:
                update.inc("collectNum", num);
                break;
            default:
                update.inc("visitNum", num);
                break;
        }
        return mongoTemplateTwo.updateFirst(query, update, VideoBo.class);
    }

    @Override
    public List<VideoBo> findVideoByIds(List<String> videoIds) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").in(videoIds));
        return mongoTemplateTwo.find(query, VideoBo.class);
    }

    @Override
    public List<VideoBo> selectClassByGroups(HashSet<String> modules, HashSet<String> classNames) {
        Criteria criteria = new Criteria("module").in(modules).and("className").in(classNames);
        MatchOperation match = Aggregation.match(criteria);
        GroupOperation groupOperation = Aggregation.group("module","className")
                .sum("visitNum").as("visitNum");
        Aggregation aggregation = Aggregation.newAggregation(match, groupOperation,
                Aggregation.sort(Sort.Direction.ASC, "module", "className"));
        AggregationResults<VideoBo> results = mongoTemplateTwo.aggregate(aggregation, "video", VideoBo.class);
        return results.getMappedResults();
    }

    @Override
    public List<VideoBo> findByLimit(HashSet<String> modules, HashSet<String> classNames, int limit) {

        GroupOperation groupOperation = Aggregation.group("module","className")
                .sum("visitNum").as("visitNum");
        Aggregation aggregation;
        if (CommonUtil.isEmpty(modules)) {
            aggregation = Aggregation.newAggregation( groupOperation,
                    Aggregation.sort(Sort.Direction.ASC, "module", "className"),
                    Aggregation.limit(limit));
        } else {
            Criteria criteria = new Criteria("module").nin(modules).and("className").nin(classNames);
            MatchOperation match = Aggregation.match(criteria);
            aggregation = Aggregation.newAggregation(match, groupOperation,
                    Aggregation.sort(Sort.Direction.ASC, "module", "className"), Aggregation.limit(limit));
        }
        AggregationResults<VideoBo> results = mongoTemplateTwo.aggregate(aggregation, "video", VideoBo.class);
        return results.getMappedResults();
    }
}


