package com.lad.dao.impl;

import com.lad.bo.InforUserReadBo;
import com.lad.dao.IInforUserReadDao;
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
 * Time:2017/10/25
 */
@Repository("inforUserReadDao")
public class InforUserReadDaoImpl implements IInforUserReadDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public InforUserReadBo addUserRead(InforUserReadBo userReadBo) {
        mongoTemplate.insert(userReadBo);
        return userReadBo;
    }

    @Override
    public InforUserReadBo findUserReadByUserid(String userid) {
        Query query = new Query();
        query.addCriteria(new Criteria("userid").is(userid));
        query.addCriteria(new Criteria("deleted").is(Constant.ACTIVITY));
        return mongoTemplate.findOne(query, InforUserReadBo.class);
    }

    @Override
    public WriteResult updateUserRead(String id, int type, LinkedHashSet<String> modules) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        if (Constant.INFOR_HEALTH == type) {
            update.set("healths", modules);
        } else if (Constant.INFOR_SECRITY == type){
            update.set("securitys", modules);
        } else if (Constant.INFOR_RADIO == type){
            update.set("radios", modules);
        } else if (Constant.INFOR_VIDEO == type){
            update.set("videos", modules);
        }
        return mongoTemplate.updateFirst(query, update, InforUserReadBo.class);
    }

    @Override
    public WriteResult deleteUserRead(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        Update update = new Update();
        update.set("deleted", Constant.DELETED);
        return mongoTemplate.updateFirst(query, update, InforUserReadBo.class);
    }

    @Override
    public WriteResult updateUserReadAll(InforUserReadBo userReadBo) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(userReadBo.getId()));
        Update update = new Update();
        update.set("healths", userReadBo.getHealths());
        update.set("securitys", userReadBo.getSecuritys());
        update.set("radios", userReadBo.getRadios());
        update.set("videos", userReadBo.getVideos());
        return mongoTemplate.updateFirst(query, update, InforUserReadBo.class);
    }
}
