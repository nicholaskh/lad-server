package com.lad.dao.impl;

import com.lad.bo.InforGroupRecomBo;
import com.lad.dao.IInforGroupRecomDao;
import com.lad.util.CommonUtil;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/12/10
 */
@Repository("inforGroupRecomDao")
public class InforGroupRecomDaoImpl implements IInforGroupRecomDao {

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public InforGroupRecomBo addInforGroup(InforGroupRecomBo groupRecomBo) {
        mongoTemplate.insert(groupRecomBo);
        return groupRecomBo;
    }

    @Override
    public InforGroupRecomBo findInforGroup(String module, String className, int type) {
        Query query = new Query();
        query.addCriteria(new Criteria("module").is(module).and("className")
                .is(className).and("type").is(type));
        return mongoTemplate.findOne(query, InforGroupRecomBo.class);
    }

    @Override
    public InforGroupRecomBo findInforGroup(String module, int type) {
        Query query = new Query();
        query.addCriteria(new Criteria("module").is(module).and("type").is(type));
        return mongoTemplate.findOne(query, InforGroupRecomBo.class);
    }

    @Override
    public List<InforGroupRecomBo> findInforGroupByModule(int type, LinkedHashSet<String> modules) {
        Query query = new Query();
        query.addCriteria(new Criteria("module").in(modules).and("type").is(type));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "halfyearNum")));
        query.limit(50);
        return mongoTemplate.find(query, InforGroupRecomBo.class);
    }

    @Override
    public List<InforGroupRecomBo> findInforGroupWithoutModule(int type, LinkedHashSet<String> modules, int limit) {
        Query query = new Query();
        if (!CommonUtil.isEmpty(modules)) {
            query.addCriteria(new Criteria("module").is(type));
        }
        query.addCriteria(new Criteria("type").is(type));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "halfyearNum")));
        query.limit(limit);
        return mongoTemplate.find(query, InforGroupRecomBo.class);
    }

    @Override
    public WriteResult updateInforGroup(String id, int halfNum, int totalNum) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.inc("halfyearNum", halfNum);
        update.inc("totalNum", totalNum);
        return mongoTemplate.updateFirst(query, update, InforGroupRecomBo.class);
    }
}
