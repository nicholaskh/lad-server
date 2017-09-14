package com.lad.dao.impl;

import com.lad.bo.UserTasteBo;
import com.lad.dao.IUserTasteDao;
import com.lad.util.Constant;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/14
 */
@Repository("userTasteDao")
public class UserTasteDaoImpl implements IUserTasteDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public UserTasteBo add(UserTasteBo tasteBo) {
        mongoTemplate.insert(tasteBo);
        return tasteBo;
    }

    @Override
    public WriteResult updateSport(String id, LinkedHashSet<String> sports) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.set("sports", sports);
        return mongoTemplate.updateFirst(query, update, UserTasteBo.class);
    }

    @Override
    public WriteResult updateMusic(String id, LinkedHashSet<String> musics) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.set("musics", musics);
        return mongoTemplate.updateFirst(query, update, UserTasteBo.class);
    }

    @Override
    public WriteResult updateLife(String id, LinkedHashSet<String> lifes) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.set("lifes", lifes);
        return mongoTemplate.updateFirst(query, update, UserTasteBo.class);
    }

    @Override
    public WriteResult updateTrip(String id, LinkedHashSet<String> trips) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.set("trips", trips);
        return mongoTemplate.updateFirst(query, update, UserTasteBo.class);
    }

    @Override
    public UserTasteBo findByUserid(String userid) {
        Query query = new Query();
        query.addCriteria(new Criteria("userid").is(userid));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        return mongoTemplate.findOne(query, UserTasteBo.class);
    }
}
