package com.lad.dao.impl;

import com.lad.dao.IBroadcastDao;
import com.lad.scrapybo.BroadcastBo;
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
import java.util.regex.Pattern;

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
        return results != null ? results.getMappedResults() : null;
    }

    @Override
    public List<BroadcastBo> findByPage(String groupName, int page, int limit) {
        Query query = new Query();
        query.addCriteria(new Criteria("module").is(groupName));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "random_num")));
        if (page < 1) {
            page = 1;
        }
        query.skip((page -1) * limit);
        query.limit(limit);
        return mongoTemplateTwo.find(query, BroadcastBo.class);
    }

    @Override
    public List<BroadcastBo> selectClassByGroups(String groupName) {
        Criteria criteria = new Criteria("module").is(groupName);
        MatchOperation match = Aggregation.match(criteria);
        GroupOperation groupOperation = Aggregation.group("module","className","source")
                .sum("visitNum").as("visitNum").count().as("totalNum");
        Aggregation aggregation = Aggregation.newAggregation(match, groupOperation,
                Aggregation.sort(Sort.Direction.ASC, "className"));
        AggregationResults<BroadcastBo> results = mongoTemplateTwo.aggregate(aggregation, "broadcast", BroadcastBo.class);
        return results != null ? results.getMappedResults() : null;
}

    public List<BroadcastBo> findByClassName(String groupName, String className) {
        Query query = new Query();
        query.addCriteria(new Criteria("module").is(groupName));
        if (StringUtils.isNotEmpty(className)) {
            query.addCriteria(new Criteria("className").is(className));
        }
        query.with(new Sort(new Sort.Order(Sort.Direction.ASC,"edition")));
        query.with(new Sort(new Sort.Order(Sort.Direction.ASC,"title")));
        return mongoTemplateTwo.find(query, BroadcastBo.class);
    }

    public List<BroadcastBo> findByClassNamePage(String groupName, String className, int start, int end) {
        Query query = new Query();
        query.addCriteria(new Criteria("module").is(groupName));
        if (StringUtils.isNotEmpty(className)) {
            query.addCriteria(new Criteria("className").is(className));
        }
        query.with(new Sort(new Sort.Order(Sort.Direction.ASC,"edition")));
        query.with(new Sort(new Sort.Order(Sort.Direction.ASC,"title")));
        if (start != 0 && end > 0) {
            start = start < 1 ? 1 : start;
            query.skip(start - 1);
            query.limit(end - start + 1);
        }
        return mongoTemplateTwo.find(query, BroadcastBo.class);
    }

    @Override
    public long findByClassNamePage(String groupName, String className) {
        Query query = new Query();
        query.addCriteria(new Criteria("module").is(groupName).and("className").is(className));
        return mongoTemplateTwo.count(query, BroadcastBo.class);
    }

    @Override
    public List<BroadcastBo> findByLimit(int limit) {
        Query query = new Query();
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "_id")));
        query.limit(limit);
        return mongoTemplateTwo.find(query, BroadcastBo.class);
    }

    @Override
    public WriteResult updateRadioNum(String radioid, int type, int num) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(radioid));
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
        return mongoTemplateTwo.updateFirst(query, update, BroadcastBo.class);
    }

    @Override
    public List<BroadcastBo> findRadioByIds(List<String> radioIds) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").in(radioIds));
        return mongoTemplateTwo.find(query, BroadcastBo.class);
    }

    @Override
    public List<BroadcastBo> selectClassByGroups(HashSet<String> modules, HashSet<String> classNames) {
        Criteria criteria = new Criteria("module").in(modules).and("className").in(classNames);
        MatchOperation match = Aggregation.match(criteria);
        GroupOperation groupOperation = Aggregation.group("module", "className", "source")
                .sum("visitNum").as("visitNum");
        Aggregation aggregation = Aggregation.newAggregation(match, groupOperation,
                Aggregation.sort(Sort.Direction.ASC, "module", "className"));
        AggregationResults<BroadcastBo> results = mongoTemplateTwo.aggregate(aggregation, "broadcast", BroadcastBo.class);
        return results != null ? results.getMappedResults() : null;
    }

    @Override
    public List<BroadcastBo> findByLimit(HashSet<String> modules, HashSet<String> classNames, int limit) {

        GroupOperation groupOperation = Aggregation.group("module", "className", "source")
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
        AggregationResults<BroadcastBo> results = mongoTemplateTwo.aggregate(aggregation, "broadcast", BroadcastBo.class);
        return results != null ? results.getMappedResults() : null;
    }

    @Override
    public List<BroadcastBo> findByTitle(String title, int page, int limit) {
        Pattern pattern = Pattern.compile("^.*"+title+".*$", Pattern.CASE_INSENSITIVE);
        Query query = new Query(new Criteria("title").regex(pattern));
        query.with(new Sort(Sort.Direction.ASC,"edition", "title"));
        page = page < 1 ? 1 : page;
        query.skip((page-1)*limit);
        query.limit(limit);
        return mongoTemplateTwo.find(query, BroadcastBo.class);
    }
}
