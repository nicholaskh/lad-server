package com.lad.dao.impl;

import com.lad.dao.IVideoDao;
import com.lad.scrapybo.InforBo;
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
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

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
        query.with(new Sort(new Sort.Order(Sort.Direction.ASC, "_id")));
        page = page < 1 ? 1 : page;
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
        GroupOperation groupOperation = Aggregation.group("module","className", "source")
                .sum("visitNum").as("visitNum").first("url").as("firstUrl")
                .first("_id").as("firstId").first("shareNum").as("firstShare")
                .first("commnetNum").as("firstComment").first("thumpsubNum").as("firstThump");
        Aggregation aggregation = Aggregation.newAggregation(match, groupOperation,
                Aggregation.sort(Sort.Direction.ASC, "className"));
        AggregationResults<VideoBo> results = mongoTemplateTwo.aggregate(aggregation, "video", VideoBo.class);
        return results != null ? results.getMappedResults() : null;
    }

    @Override
    public List<VideoBo> findByClassNamePage(String module, String className, int page, int limit) {
        Query query = new Query();
        query.addCriteria(new Criteria("module").is(module));
        if (StringUtils.isNotEmpty(className)) {
            query.addCriteria(new Criteria("className").is(className));
        }
        query.with(new Sort(new Sort.Order(Sort.Direction.ASC, "title")));
        page = page < 1 ? 1 : page;
        query.skip((page -1) * limit);
        query.limit(limit);
        return mongoTemplateTwo.find(query, VideoBo.class);
    }

    public List<VideoBo> findByClassNamePage(String module, String className) {
        Query query = new Query();
        query.addCriteria(new Criteria("module").is(module));
        if (StringUtils.isNotEmpty(className)) {
            query.addCriteria(new Criteria("className").is(className));
        }
        query.with(new Sort(new Sort.Order(Sort.Direction.ASC, "title")));
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
                .sum("visitNum").as("visitNum").first("url").as("firstUrl")
                .first("_id").as("firstId").first("shareNum").as("firstShare")
                .first("commnetNum").as("firstComment").first("thumpsubNum").as("firstThump");
        Aggregation aggregation = Aggregation.newAggregation(match,
                Aggregation.sort(Sort.Direction.ASC, "module","className"),
                groupOperation);
        AggregationResults<VideoBo> results = mongoTemplateTwo.aggregate(aggregation, "video", VideoBo.class);
        return results != null ? results.getMappedResults() : null;
    }

    @Override
    public List<VideoBo> findByLimit(LinkedList<String> modules, LinkedList<String> classNames, int limit) {

        GroupOperation groupOperation = Aggregation.group("module","className")
                .sum("visitNum").as("visitNum").first("url").as("firstUrl")
                .first("_id").as("firstId").first("shareNum").as("firstShare")
                .first("commnetNum").as("firstComment").first("thumpsubNum").as("firstThump");
        Aggregation aggregation;
        if (CommonUtil.isEmpty(modules)) {
            aggregation = Aggregation.newAggregation(
                    Aggregation.sort(Sort.Direction.ASC, "module", "className"), groupOperation,
                    Aggregation.limit(limit));
        } else {
            Criteria cr = new Criteria();
            int i = 0;
            Criteria[] crArrs = new Criteria[modules.size()];
            for (String module : modules) {
                Criteria criteria = new Criteria("module").is(module).and("className").is(classNames.get(i));
                crArrs[i++] = criteria;
            }
            //非条件满足
            cr.norOperator(crArrs);
            MatchOperation match = Aggregation.match(cr);
            aggregation = Aggregation.newAggregation(match,
                    Aggregation.sort(Sort.Direction.ASC, "module", "className"),  groupOperation,Aggregation.limit(limit));
        }
        AggregationResults<VideoBo> results = mongoTemplateTwo.aggregate(aggregation, "video", VideoBo.class);
        return results != null ? results.getMappedResults() : null;
    }

    @Override
    public VideoBo findVideoByFirst(String modules, String classNames) {
        Criteria criteria = new Criteria("module").is(modules).and("className").is(classNames);
        MatchOperation match = Aggregation.match(criteria);
        GroupOperation groupOperation = Aggregation.group("module","className")
                .sum("visitNum").as("visitNum").first("url").as("firstUrl")
                .first("_id").as("firstId").first("shareNum").as("firstShare")
                .first("commnetNum").as("firstComment").first("thumpsubNum").as("firstThump");
        Aggregation aggregation = Aggregation.newAggregation(match,
                Aggregation.sort(Sort.Direction.ASC, "module","className"),
                groupOperation);
        AggregationResults<VideoBo> results = mongoTemplateTwo.aggregate(aggregation, "video", VideoBo.class);
        return results != null && !CommonUtil.isEmpty(results.getMappedResults()) ? results.getMappedResults().get
                (0) : null;
    }

    @Override
    public long findByClassNameCount(String module, String className) {
        Query query = new Query();
        query.addCriteria(new Criteria("module").is(module).and("className").is(className));
        return mongoTemplateTwo.count(query, VideoBo.class);
    }

    @Override
    public List<VideoBo> findByTitleRegex(String title, int page, int limit) {
        Pattern pattern = Pattern.compile("^.*"+title+".*$", Pattern.CASE_INSENSITIVE);
        Query query = new Query(new Criteria("title").regex(pattern));
        query.with(new Sort(Sort.Direction.ASC,"num", "title"));
        page = page < 1 ? 1 : page;
        query.skip((page-1)*limit);
        query.limit(limit);
        return mongoTemplateTwo.find(query, VideoBo.class);
    }
}


