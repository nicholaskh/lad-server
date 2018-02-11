package com.lad.dao.impl;

import com.lad.dao.ISecurityDao;
import com.lad.scrapybo.BroadcastBo;
import com.lad.scrapybo.SecurityBo;
import com.lad.util.Constant;
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
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Pattern;

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

    @Override
    public WriteResult updateSecurityNum(String inforid, int type, int num) {
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
        return mongoTemplateTwo.updateFirst(query, update, SecurityBo.class);
    }

    @Override
    public List<SecurityBo> findSecurityByIds(List<String> securityIds) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").in(securityIds));
        return mongoTemplateTwo.find(query, SecurityBo.class);
    }

    @Override
    public List<SecurityBo> findByTitleRegex(String title, int page, int limit) {
        Pattern pattern = Pattern.compile("^.*"+title+".*$", Pattern.CASE_INSENSITIVE);
        Query query = new Query(new Criteria("title").regex(pattern));
        query.with(new Sort(Sort.Direction.ASC,"title"));
        page = page < 1 ? 1 : page;
        query.skip((page-1)*limit);
        query.limit(limit);
        return mongoTemplateTwo.find(query, SecurityBo.class);
    }
}
