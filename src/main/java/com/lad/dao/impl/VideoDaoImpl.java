package com.lad.dao.impl;

import com.lad.dao.IVideoDao;
import com.lad.scrapybo.VideoBo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
    public List<VideoBo> findByPage(int page, int limit) {
        Query query = new Query();
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "time")));
        if (page < 1) {
            page = 1;
        }
        query.skip((page - 1)*limit);
        query.limit(limit);
        return mongoTemplateTwo.find(query, VideoBo.class);
    }

    @Override
    public VideoBo findById(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        return mongoTemplateTwo.findOne(query, VideoBo.class);
    }
}
