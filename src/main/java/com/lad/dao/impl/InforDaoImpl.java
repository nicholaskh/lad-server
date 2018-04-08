package com.lad.dao.impl;

import com.lad.dao.IInforDao;
import com.lad.scrapybo.BroadcastBo;
import com.lad.scrapybo.InforBo;
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
        AggregationResults<InforBo> results = mongoTemplateTwo.aggregate(aggregation, "health", InforBo.class);
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
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "num")));
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


    @Override
    public List<InforBo> homeHealthRecom(int limit) {
        Query query = new Query();
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "time")));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "num")));
        query.limit(limit);
        return mongoTemplateTwo.find(query, InforBo.class);
    }

    @Override
    public List<InforBo> userHealthRecom(String userid, int limit) {
        Query query = new Query();
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "time")));
        query.limit(limit);
        return mongoTemplateTwo.find(query, InforBo.class);
    }

    @Override
    public List<InforBo> findHealthByIds(List<String> healthIds) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").in(healthIds));
        return mongoTemplateTwo.find(query, InforBo.class);
    }

    @Override
    public WriteResult updateInforNum(String inforid, int type, int num) {
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
        return mongoTemplateTwo.updateFirst(query, update, InforBo.class);
    }

    @Override
    public List<InforBo> findByTitleRegex(String title, int page, int limit) {
        Pattern pattern = Pattern.compile("^.*"+title+".*$", Pattern.CASE_INSENSITIVE);
        Query query = new Query(new Criteria("title").regex(pattern));
        query.with(new Sort(Sort.Direction.DESC,"time", "num"));
        page = page < 1 ? 1 : page;
        query.skip((page-1)*limit);
        query.limit(limit);
        return mongoTemplateTwo.find(query, InforBo.class);
    }
}
